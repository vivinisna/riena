/*******************************************************************************
 * Copyright (c) 2007, 2013 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.internal.ui.ridgets.swt;

import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.osgi.service.log.LogService;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateListStrategy;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.Observables;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.equinox.log.Logger;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.databinding.viewers.IViewerObservableList;
import org.eclipse.jface.databinding.viewers.ObservableListTreeContentProvider;
import org.eclipse.jface.databinding.viewers.TreeStructureAdvisor;
import org.eclipse.jface.databinding.viewers.ViewersObservables;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import org.eclipse.riena.core.Log4r;
import org.eclipse.riena.core.util.ListenerList;
import org.eclipse.riena.core.util.ReflectionUtils;
import org.eclipse.riena.core.util.StringUtils;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IColumnFormatter;
import org.eclipse.riena.ui.ridgets.IMarkableRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget;
import org.eclipse.riena.ui.ridgets.ITreeRidget;
import org.eclipse.riena.ui.ridgets.ITreeTableRidget;
import org.eclipse.riena.ui.ridgets.swt.AbstractSWTWidgetRidget;
import org.eclipse.riena.ui.ridgets.swt.AbstractSelectableRidget;
import org.eclipse.riena.ui.ridgets.swt.MarkerSupport;
import org.eclipse.riena.ui.swt.facades.SWTFacade;

/**
 * Ridget for SWT {@link Tree} widgets.
 */
public class TreeRidget extends AbstractSelectableRidget implements ITreeRidget {

	private static final Listener ITEM_ERASER_AND_PAINTER = SWTFacade.getDefault().createTreeItemEraserAndPainter();
	private static final String PREFIX_IS = "is"; //$NON-NLS-1$

	private final SelectionListener selectionTypeEnforcer;
	private final DoubleClickForwarder doubleClickForwarder;
	private final Queue<ExpansionCommand> expansionStack;
	private ListenerList<IActionListener> doubleClickListeners;

	private DataBindingContext dbc;
	/*
	 * Binds the viewer's multiple selection to the multiple selection observable. This binding has to be disposed when the ridget is set to output-only, to
	 * avoid updating the model. It has to be recreated when the ridget is set to not-output-only.
	 */
	private Binding viewerMSB;

	private TreeViewer viewer;
	/* keeps the last legal selection when in 'output only' mode */
	private TreeItem[] savedSelection;

	/*
	 * The original array of elements given as input to the ridget via the #bindToModel method. The ridget however works with the copy (treeRoots) in order to
	 * be independent of modification to the original array.
	 * 
	 * Calling #updateFromModel will synchronize the treeRoots array with the model array.
	 */
	private Object[] model;
	private Object[] treeRoots;
	private Class<? extends Object> treeElementClass;
	private String childrenAccessor;
	private String parentAccessor;
	private String[] valueAccessors;
	private String[] columnHeaders;
	private String enablementAccessor;
	private String visibilityAccessor;
	private String imageAccessor;
	private String openNodeImageAccessor;
	private String checkExpandedMethod;
	private boolean showRoots = true;
	private StructuredViewerFilterHolder filterHolder;

	public TreeRidget() {
		selectionTypeEnforcer = new SelectionTypeEnforcer();
		doubleClickForwarder = new DoubleClickForwarder();
		expansionStack = new LinkedList<ExpansionCommand>();
		addPropertyChangeListener(IRidget.PROPERTY_ENABLED, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				applyEraseListener();
			}
		});
		addPropertyChangeListener(IMarkableRidget.PROPERTY_OUTPUT_ONLY, new PropertyChangeListener() {
			public void propertyChange(final PropertyChangeEvent evt) {
				saveSelection();
				if (isOutputOnly()) {
					disposeMultipleSelectionBinding();
				} else {
					createMultipleSelectionBinding();
				}
			}
		});
	}

	@Override
	protected void bindUIControl() {
		final Tree control = getUIControl();
		if (control != null && treeRoots != null) {
			applyColumns(control);
			bindToViewer(control);
			bindToSelection();
			control.addSelectionListener(selectionTypeEnforcer);
			control.addMouseListener(doubleClickForwarder);
			updateExpansionState();
			applyEraseListener();
			applyTableColumnHeaders(control);
			getFilterHolder().activate(viewer);
		}
	}

	@Override
	protected void checkUIControl(final Object uiControl) {
		checkType(uiControl, Tree.class);
	}

	@Override
	protected void unbindUIControl() {
		super.unbindUIControl();
		getFilterHolder().deactivate(viewer);
		if (viewer != null) {
			final Object[] elements = viewer.getExpandedElements();
			final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.RESTORE, elements);
			expansionStack.add(cmd);
		}
		if (dbc != null) {
			disposeMultipleSelectionBinding();
			dbc.dispose();
			dbc = null;
		}
		final Tree control = getUIControl();
		if (control != null) {
			control.removeSelectionListener(selectionTypeEnforcer);
			control.removeMouseListener(doubleClickForwarder);
			final SWTFacade facade = SWTFacade.getDefault();
			facade.removeEraseItemListener(control, ITEM_ERASER_AND_PAINTER);
			facade.removePaintItemListener(control, ITEM_ERASER_AND_PAINTER);
		}
		if (viewer != null) {
			// IMPORTANT: remove the change listeners from the input model.
			// Has to happen after disposing the binding to avoid affecting
			// the selection.
			// See also https://bugs.eclipse.org/243374
			viewer.setInput(null);
		}
		viewer = null;
	}

	@Override
	protected final List<?> getRowObservables() {
		List<?> result = null;
		if (viewer != null) { // have roots and control
			final ObservableListTreeContentProvider cp = (ObservableListTreeContentProvider) viewer.getContentProvider();
			result = new ArrayList<Object>(cp.getKnownElements());
		} else if (treeRoots != null) { // have roots only
			result = collectAllElements();
		}
		return result;
	}

	protected void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String[] valueAccessors, final String[] columnHeaders, final String enablementAccessor,
			final String visibilityAccessor, final String imageAccessor, final String openNodeImageAccessor) {

		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, valueAccessors, columnHeaders, enablementAccessor, visibilityAccessor,
				imageAccessor, openNodeImageAccessor, null);
	}

	protected void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String[] valueAccessors, final String[] columnHeaders, final String enablementAccessor,
			final String visibilityAccessor, final String imageAccessor, final String openNodeImageAccessor, final String expandedAccessor) {
		Assert.isNotNull(treeRoots);
		Assert.isNotNull(treeElementClass);
		Assert.isNotNull(childrenAccessor);
		Assert.isNotNull(parentAccessor);
		Assert.isNotNull(valueAccessors);
		Assert.isLegal(valueAccessors.length > 0, "valueAccessors must have at least one entry"); //$NON-NLS-1$
		if (columnHeaders != null) {
			final String msg = "Mismatch between number of valueAccessors and columnHeaders"; //$NON-NLS-1$
			Assert.isLegal(valueAccessors.length == columnHeaders.length, msg);
		}

		unbindUIControl();

		this.model = treeRoots;
		this.treeRoots = new Object[model.length];
		System.arraycopy(model, 0, this.treeRoots, 0, this.treeRoots.length);
		this.treeElementClass = treeElementClass;
		this.childrenAccessor = childrenAccessor;
		this.parentAccessor = parentAccessor;
		this.valueAccessors = new String[valueAccessors.length];
		System.arraycopy(valueAccessors, 0, this.valueAccessors, 0, this.valueAccessors.length);
		if (columnHeaders != null) {
			this.columnHeaders = new String[columnHeaders.length];
			System.arraycopy(columnHeaders, 0, this.columnHeaders, 0, this.columnHeaders.length);
		} else {
			this.columnHeaders = null;
		}
		this.enablementAccessor = enablementAccessor;
		this.visibilityAccessor = visibilityAccessor;
		this.imageAccessor = imageAccessor;
		this.openNodeImageAccessor = openNodeImageAccessor;
		if (expandedAccessor != null) {
			this.checkExpandedMethod = PREFIX_IS + StringUtils.capitalize(expandedAccessor);
		}

		initializeExpansionStack();

		bindUIControl();
	}

	private void initializeExpansionStack() {
		expansionStack.clear();
		if (treeRoots.length == 1) {
			addExpansionCommand(treeRoots[0]);
			if (checkExpandedMethod != null) {
				addExpansionCommandsForRootDescendants(treeRoots[0]);
			}
		}
	}

	/**
	 * Adds commands for expansion AND collapsion for all descendants of the given root element to the <code>expansionStack</code> .
	 * 
	 * @param element
	 *            root element
	 */
	private void addExpansionCommandsForRootDescendants(final Object element) {
		final List<Object> allDescendants = new ArrayList<Object>();
		collectChildren(element, allDescendants);
		for (final Object descendant : allDescendants) {
			if (isLeaf(descendant)) {
				continue;
			}
			if (isExpanded(descendant)) {
				addExpansionCommand(descendant);
			} else {
				addCollapseCommand(descendant);
			}
		}
	}

	private boolean isLeaf(final Object element) {
		final List<?> children = getChildren(element);
		return children.isEmpty();
	}

	private boolean isExpanded(final Object element) {
		return ReflectionUtils.invoke(element, checkExpandedMethod);
	}

	private void addExpansionCommand(final Object element) {
		final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.EXPAND, element);
		expansionStack.add(cmd);
	}

	private void addCollapseCommand(final Object element) {
		final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.COLLAPSE, element);
		expansionStack.add(cmd);
	}

	/**
	 * Returns the TreeViewer instance used by this ridget or null.
	 */
	protected final TreeViewer getViewer() {
		return viewer;
	}

	/**
	 * Returns the column formatters for this ridget. Each entry in the array corresponds to a column (i.e. 0 for the 1st column, 1 for the 2nd, etc). If a
	 * column has no formatter associated, the array entry will be null. The array has the length {@code numColumns}.
	 * <p>
	 * Implementation note: This ridget does not support columns, so this array will be filled with null entries. Subclasses that support column formatters must
	 * override to return an appropriate array.
	 * 
	 * @param numColumns
	 *            return the number of columns, an integer >= 0.
	 * @return an array of IColumnFormatter, never null
	 */
	protected IColumnFormatter[] getColumnFormatters(final int numColumns) {
		return new IColumnFormatter[numColumns];
	}

	/**
	 * This method changes the width of the Tree's columns.
	 * <p>
	 * Does nothing by default. Subclasses implementing {@link ITreeTableRidget} must override.
	 * 
	 * @param control
	 *            the Tree control; never null
	 */
	protected void applyColumnWidths(final Tree control) {
		// subclasses should override
	}

	// public methods
	// ///////////////

	@Override
	public Tree getUIControl() {
		return (Tree) super.getUIControl();
	}

	@Override
	public void addDoubleClickListener(final IActionListener listener) {
		Assert.isNotNull(listener, "listener is null"); //$NON-NLS-1$
		if (doubleClickListeners == null) {
			doubleClickListeners = new ListenerList<IActionListener>(IActionListener.class);
		}
		doubleClickListeners.add(listener);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation will try to expand the path to the give option, to ensure that the corresponding tree element exists.
	 */
	@Override
	public boolean containsOption(final Object option) {
		reveal(new Object[] { option });
		return super.containsOption(option);
	}

	public void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String valueAccessor) {
		Assert.isNotNull(valueAccessor);
		final String[] myValueAccessors = new String[] { valueAccessor };
		final String[] noColumnHeaders = null;
		final String noEnablementAccessor = null;
		final String noVisibilityAccessor = null;
		final String noImageAccessor = null;
		final String noOpenNodeImageAccessor = null;
		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, myValueAccessors, noColumnHeaders, noEnablementAccessor,
				noVisibilityAccessor, noImageAccessor, noOpenNodeImageAccessor);
	}

	public void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String valueAccessor, final String enablementAccessor, final String visibilityAccessor) {
		Assert.isNotNull(valueAccessor);
		final String[] myValueAccessors = new String[] { valueAccessor };
		final String[] noColumnHeaders = null;
		final String noImageAccessor = null;
		final String noOpenNodeImageAccessor = null;
		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, myValueAccessors, noColumnHeaders, enablementAccessor,
				visibilityAccessor, noImageAccessor, noOpenNodeImageAccessor);
	}

	public void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String valueAccessor, final String enablementAccessor, final String visibilityAccessor,
			final String imageAccessor) {
		Assert.isNotNull(valueAccessor);
		final String[] myValueAccessors = new String[] { valueAccessor };
		final String[] noColumnHeaders = null;
		final String noOpenNodeImageAccessor = null;
		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, myValueAccessors, noColumnHeaders, enablementAccessor,
				visibilityAccessor, imageAccessor, noOpenNodeImageAccessor);
	}

	public void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String valueAccessor, final String enablementAccessor, final String visibilityAccessor,
			final String imageAccessor, final String openNodeImageAccessor) {
		Assert.isNotNull(valueAccessor);
		final String[] myValueAccessors = new String[] { valueAccessor };
		final String[] noColumnHeaders = null;
		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, myValueAccessors, noColumnHeaders, enablementAccessor,
				visibilityAccessor, imageAccessor, openNodeImageAccessor);
	}

	public void bindToModel(final Object[] treeRoots, final Class<? extends Object> treeElementClass, final String childrenAccessor,
			final String parentAccessor, final String valueAccessor, final String enablementAccessor, final String visibilityAccessor,
			final String imageAccessor, final String openNodeImageAccessor, final String expandedAccessor) {
		Assert.isNotNull(valueAccessor);
		final String[] myValueAccessors = new String[] { valueAccessor };
		final String[] noColumnHeaders = null;
		this.bindToModel(treeRoots, treeElementClass, childrenAccessor, parentAccessor, myValueAccessors, noColumnHeaders, enablementAccessor,
				visibilityAccessor, imageAccessor, openNodeImageAccessor, expandedAccessor);
	}

	public void collapse(final Object element) {
		addCollapseCommand(element);
		final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.COLLAPSE, element);
		expansionStack.add(cmd);
		updateExpansionState();
	}

	public void collapseAll() {
		final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.FULLY_COLLAPSE, null);
		expansionStack.add(cmd);
		updateExpansionState();
	}

	public void expand(final Object element) {
		addExpansionCommand(element);
		updateExpansionState();
	}

	public void expandAll() {
		final ExpansionCommand cmd = new ExpansionCommand(ExpansionState.FULLY_EXPAND, null);
		expansionStack.add(cmd);
		updateExpansionState();
	}

	public boolean getRootsVisible() {
		return showRoots;
	}

	/**
	 * Always returns true because mandatory markers do not make sense for this ridget.
	 */
	@Override
	public boolean isDisableMandatoryMarker() {
		return true;
	}

	public void refresh(final Object node) {
		if (viewer != null) {
			viewer.refresh(node, true);
		}
	}

	@Override
	public void removeDoubleClickListener(final IActionListener listener) {
		if (doubleClickListeners != null) {
			doubleClickListeners.remove(listener);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * For each selection candidate in the List <tt>newSelection</tt>, this implementation will try to expand the path to the corresponding tree node, to ensure
	 * that the corresponding tree element is selectable.
	 */
	@Override
	public final void setSelection(final List<?> newSelection) {
		reveal(newSelection.toArray());
		super.setSelection(newSelection);
		saveSelection();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Implementation notes:
	 * <ul>
	 * <li>If showRoots is false, the children of the first entry in the array of treeRoots will be shown at level-0 of the tree</li>
	 * <li>This method must be ivoked before calling bindToModel(...). If changed afterwards it requires a call to bindToModel() or updateFromModel() to take
	 * effect.</li>
	 * </ul>
	 */
	public void setRootsVisible(final boolean showRoots) {
		firePropertyChange(ITreeRidget.PROPERTY_ROOTS_VISIBLE, this.showRoots, this.showRoots = showRoots);
	}

	@Override
	public void updateFromModel() {
		treeRoots = new Object[model.length];
		System.arraycopy(model, 0, treeRoots, 0, treeRoots.length);
		final List<Object> selection = getSelection();
		if (viewer != null) {
			final Object[] expandedElements = viewer.getExpandedElements();
			viewer.getControl().setRedraw(false);
			try {
				// IMPORTANT: next line removes listeners from old model
				viewer.setInput(null);
				if (showRoots) {
					viewer.setInput(treeRoots);
				} else {
					final FakeRoot fakeRoot = new FakeRoot(treeRoots.length > 0 ? treeRoots[0] : null, childrenAccessor);
					viewer.setInput(fakeRoot);
				}
				viewer.setExpandedElements(expandedElements);
				// update column specific formatters
				final TreeRidgetLabelProvider labelProvider = (TreeRidgetLabelProvider) viewer.getLabelProvider();
				final IColumnFormatter[] formatters = getColumnFormatters(labelProvider.getColumnCount());
				labelProvider.setFormatters(formatters);
				// update expanded/collapsed icons
				viewer.refresh();
				viewer.setSelection(new StructuredSelection(selection));
			} finally {
				viewer.getControl().setRedraw(true);
			}
		} else {
			setSelection(selection);
		}
	}

	private void applyColumns(final Tree control) {
		final int columnCount = control.getColumnCount() == 0 ? 1 : control.getColumnCount();
		final int expectedCols = valueAccessors.length;
		if (columnCount != expectedCols) {
			for (final TreeColumn column : control.getColumns()) {
				column.dispose();
			}
			for (int i = 0; i < expectedCols; i++) {
				new TreeColumn(control, SWT.NONE);
			}
			applyColumnWidths(control);
		}
	}

	private void applyEraseListener() {
		if (viewer != null) {
			final Tree control = viewer.getTree();
			final SWTFacade facade = SWTFacade.getDefault();
			facade.removeEraseItemListener(control, ITEM_ERASER_AND_PAINTER);
			facade.removePaintItemListener(control, ITEM_ERASER_AND_PAINTER);
			if (!isEnabled() && MarkerSupport.isHideDisabledRidgetContent()) {
				facade.addEraseItemListener(control, ITEM_ERASER_AND_PAINTER);
				facade.addPaintItemListener(control, ITEM_ERASER_AND_PAINTER);
			}
		}
	}

	private void applyTableColumnHeaders(final Tree control) {
		final boolean headersVisible = columnHeaders != null;
		control.setHeaderVisible(headersVisible);
		if (headersVisible) {
			final TreeColumn[] columns = control.getColumns();
			for (int i = 0; i < columns.length; i++) {
				String columnHeader = ""; //$NON-NLS-1$
				if (i < columnHeaders.length && columnHeaders[i] != null) {
					columnHeader = columnHeaders[i];
				}
				columns[i].setText(columnHeader);
			}
		}
	}

	/**
	 * Initialize databinding for tree viewer.
	 */
	private void bindToViewer(final Tree control) {
		viewer = new SharedControlTreeViewer(control);

		// how to create the content/structure for the tree
		final TreeStructureAdvisor structureAdvisor = createStructureAdvisor();
		final ObservableListTreeContentProvider viewerCP = createContentProvider(structureAdvisor); // one instance per viewer instance

		// refresh icons on addition / removal
		viewer.setContentProvider(viewerCP);
		viewerCP.getKnownElements().addSetChangeListener(new TreeContentChangeListener(viewer, structureAdvisor));

		// labels
		final IColumnFormatter[] formatters = getColumnFormatters(valueAccessors.length);
		final ILabelProvider viewerLP = TreeRidgetLabelProvider.createLabelProvider(viewer, treeElementClass, viewerCP.getKnownElements(), valueAccessors,
				enablementAccessor, imageAccessor, openNodeImageAccessor, formatters);
		viewer.setLabelProvider(viewerLP);

		// input
		if (showRoots) {
			viewer.setInput(treeRoots);
		} else {
			final FakeRoot fakeRoot = new FakeRoot(treeRoots.length > 0 ? treeRoots[0] : null, childrenAccessor);
			viewer.setInput(fakeRoot);
		}
		final IObservableMap enablementAttr = createObservableAttribute(viewerCP, enablementAccessor);
		preventDisabledItemSelection(enablementAttr);
		final IObservableMap visibilityAttr = createObservableAttribute(viewerCP, visibilityAccessor);
		monitorVisibility(viewer, structureAdvisor, visibilityAttr);
	}

	/**
	 * Initialize databinding related to selection handling (single/multi).
	 */
	private void bindToSelection() {
		dbc = new DataBindingContext();
		// viewer to single selection binding
		final IObservableValue viewerSelection = ViewersObservables.observeSingleSelection(viewer);
		dbc.bindValue(viewerSelection, getSingleSelectionObservable(), new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE)
				.setAfterGetValidator(new OutputAwareValidator(this)), new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE));
		// viewer to multi selection binding
		viewerMSB = null;
		if (!isOutputOnly()) {
			createMultipleSelectionBinding();
		}
		saveSelection();
	}

	private List<?> collectAllElements() {
		final List<Object> allElements = new ArrayList<Object>();
		for (final Object root : treeRoots) {
			if (root != null) {
				if (showRoots) {
					allElements.add(root);
				}
				collectChildren(root, allElements);
			}
		}
		return allElements;
	}

	/**
	 * Collects all children of the given parent and adds them to the given list.
	 * <p>
	 * <b>Note</b>: First the leaf of a sub-tree is added, than the parent of the leaf, etc. till the root of the sub-tree.
	 * 
	 * @param parent
	 *            parent element
	 * @param result
	 *            list of all children
	 */
	private void collectChildren(final Object parent, final List<Object> result) {
		final List<?> children = getChildren(parent);
		for (final Object child : children) {
			if (child == null) {
				continue;
			}
			collectChildren(child, result);
			result.add(child);
		}
	}

	private List<?> getChildren(final Object parent) {
		final String methodName = "get" + StringUtils.capitalize(childrenAccessor); //$NON-NLS-1$
		final List<?> children = ReflectionUtils.invoke(parent, methodName);
		return children;
	}

	private ObservableListTreeContentProvider createContentProvider(final TreeStructureAdvisor structureAdvisor) {
		final Realm realm = SWTObservables.getRealm(Display.getDefault());
		// how to obtain an observable list of children from a given object (expansion)
		final IObservableFactory listFactory = new IObservableFactory() {
			public IObservable createObservable(final Object target) {
				if (target instanceof Object[]) {
					return Observables.staticObservableList(realm, Arrays.asList((Object[]) target));
				}
				Object value;
				if (target instanceof FakeRoot) {
					value = ((FakeRoot) target).getRoot();
					if (value == null) {
						return new ObservableList(Collections.EMPTY_LIST, treeElementClass) {
							// empty list
						};
					}
				} else {
					value = target;
				}
				if (AbstractSWTWidgetRidget.isBean(treeElementClass)) {
					return BeansObservables.observeList(realm, value, childrenAccessor, treeElementClass);
				} else {
					return PojoObservables.observeList(realm, value, childrenAccessor, treeElementClass);
				}
			}
		};

		// how to create the content/structure for the tree
		return new ObservableListTreeContentProvider(listFactory, structureAdvisor);
	}

	private void createMultipleSelectionBinding() {
		if (viewerMSB == null && dbc != null && viewer != null) {
			final StructuredSelection currentSelection = new StructuredSelection(getSelection());
			final IViewerObservableList viewerSelections = ViewersObservables.observeMultiSelection(viewer);
			viewerMSB = dbc.bindList(viewerSelections, getMultiSelectionObservable(), new UpdateListStrategy(UpdateListStrategy.POLICY_UPDATE),
					new UpdateListStrategy(UpdateListStrategy.POLICY_UPDATE));
			viewer.setSelection(currentSelection);
		}
	}

	private IObservableMap createObservableAttribute(final ObservableListTreeContentProvider viewerCP, final String accessor) {
		IObservableMap result = null;
		if (accessor != null) {
			if (AbstractSWTWidgetRidget.isBean(treeElementClass)) {
				result = BeansObservables.observeMap(viewerCP.getKnownElements(), treeElementClass, accessor);
			} else {
				result = PojoObservables.observeMap(viewerCP.getKnownElements(), treeElementClass, accessor);
			}
		}
		return result;
	}

	private TreeStructureAdvisor createStructureAdvisor() {
		// how to get the parent from a given object
		return new GenericTreeStructureAdvisor(parentAccessor, treeElementClass);
	}

	private void disposeMultipleSelectionBinding() {
		if (viewerMSB != null) { // implies dbc != null
			viewerMSB.dispose();
			dbc.removeBinding(viewerMSB);
			viewerMSB = null;
		}
	}

	/**
	 * Filters out elements that are not visible. Monitors element visibility and updates the tree ridget.
	 */
	private void monitorVisibility(final TreeViewer viewer, final TreeStructureAdvisor structureAdvisor, final IObservableMap visibilityAttr) {
		if (visibilityAttr != null) {
			viewer.addFilter(new ViewerFilter() {
				@Override
				public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
					final Object visible = visibilityAttr.get(element);
					return Boolean.FALSE.equals(visible) ? false : true;
				}
			});
			final IMapChangeListener mapChangeListener = new IMapChangeListener() {
				public void handleMapChange(final MapChangeEvent event) {
					final Set<?> affectedElements = event.diff.getChangedKeys();
					for (final Object element : affectedElements) {
						final Object parent = structureAdvisor.getParent(element);
						if (parent == null || treeRoots.length == 0 || (parent == treeRoots[0] && !showRoots)) {
							viewer.refresh();
						} else {
							viewer.refresh(parent);
						}
					}
				}
			};
			visibilityAttr.addMapChangeListener(mapChangeListener);
		}
	}

	/**
	 * Prevent disabled items from being selected. This listener is executed before the SelectionTypeEnforcer.
	 */
	private void preventDisabledItemSelection(final IObservableMap enablementAttr) {
		if (enablementAttr != null) {
			viewer.addSelectionChangedListener(new ISelectionChangedListener() {
				/* Holds the last selection. */
				private List<Object> lastSel;

				public void selectionChanged(final SelectionChangedEvent event) {
					final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					final List<Object> newSel = new ArrayList<Object>(selection.toList());
					boolean changed = false;
					for (final Object element : selection.toArray()) {
						final Object isEnabled = enablementAttr.get(element);
						if (Boolean.FALSE.equals(isEnabled)) {
							newSel.remove(element);
							changed = true;
						}
					}
					if (changed) {
						/*
						 * If the current selection is empty after rejecting disabled elements, restore the last selection.
						 */
						if (newSel.isEmpty() && lastSel != null) {
							viewer.setSelection(new StructuredSelection(lastSel));
							setSelection(lastSel);
						} else {
							viewer.setSelection(new StructuredSelection(newSel));
							setSelection(newSel);
							lastSel = newSel;
						}
					} else {
						lastSel = newSel;
					}
				}
			});
		}
	}

	/**
	 * Expand tree paths to candidates before selecting them. This ensures the tree items to the candidates are created and the candidates become
	 * "known elements" (if they exist).
	 */
	private void reveal(final Object[] candidates) {
		if (viewer != null) {
			final Control control = viewer.getControl();
			control.setRedraw(false);
			try {
				for (final Object candidate : candidates) {
					viewer.expandToLevel(candidate, 0);
				}
			} finally {
				control.setRedraw(true);
			}
		}
	}

	/**
	 * Take a snapshot of the selection in the tree widget.
	 */
	private synchronized void saveSelection() {
		if (viewer != null && isOutputOnly()) {
			// only save selection when in 'output only' mode
			savedSelection = viewer.getTree().getSelection();
		} else {
			savedSelection = new TreeItem[0];
		}
	}

	/**
	 * Resets the selection in the tree widget to the last saved selection.
	 */
	private synchronized void restoreSelection() {
		if (viewer != null) {
			final Tree control = viewer.getTree();
			control.deselectAll();
			for (final TreeItem item : savedSelection) {
				// use select to avoid scrolling the tree
				control.select(item);
			}
		}
	}

	/**
	 * Updates the expand / collapse state of the viewers model, based on a FIFO queue of {@link ExpansionCommand}s.
	 */
	private void updateExpansionState() {
		if (viewer != null) {
			viewer.getControl().setRedraw(false);
			try {
				while (!expansionStack.isEmpty()) {
					final ExpansionCommand cmd = expansionStack.remove();
					final ExpansionState state = cmd.state;
					if (state == ExpansionState.FULLY_COLLAPSE) {
						final Object[] expanded = viewer.getExpandedElements();
						viewer.collapseAll();
						for (final Object wasExpanded : expanded) {
							viewer.update(wasExpanded, null); // update icon
						}
					} else if (state == ExpansionState.FULLY_EXPAND) {
						viewer.expandAll();
						viewer.refresh(); // update all icons
					} else if (state == ExpansionState.COLLAPSE) {
						viewer.collapseToLevel(cmd.element, 1);
						viewer.update(cmd.element, null); // update icon
					} else if (state == ExpansionState.EXPAND) {
						viewer.expandToLevel(cmd.element, 1);
						viewer.update(cmd.element, null); // update icon
					} else if (state == ExpansionState.RESTORE) {
						final Object[] elements = (Object[]) cmd.element;
						viewer.setExpandedElements(elements);
					} else {
						final String errorMsg = "unknown expansion state: " + state; //$NON-NLS-1$
						throw new IllegalStateException(errorMsg);
					}
				}

			} finally {
				viewer.getControl().setRedraw(true);
			}
		}
	}

	@Override
	protected StructuredViewerFilterHolder getFilterHolder() {
		if (filterHolder == null) {
			filterHolder = new StructuredViewerFilterHolder();
		}
		return filterHolder;
	}

	// helping classes
	// ////////////////

	/**
	 * A {@link TreeViewer} that honors the current binding state of the Ridget
	 */
	private final class SharedControlTreeViewer extends TreeViewer {

		private SharedControlTreeViewer(final Tree tree) {
			super(tree);
		}

		@Override
		protected void handleTreeExpand(final org.eclipse.swt.events.TreeEvent event) {
			if (getUIControl() != null) {
				if (!event.item.isDisposed()) {
					super.handleTreeExpand(event);
				}
			}
		}

		@Override
		protected void handleTreeCollapse(final org.eclipse.swt.events.TreeEvent event) {
			if (getUIControl() != null) {
				super.handleTreeCollapse(event);
			}
		}
	}

	/**
	 * Enumeration with the expansion states of this ridget.
	 */
	private enum ExpansionState {
		FULLY_COLLAPSE, FULLY_EXPAND, COLLAPSE, EXPAND, RESTORE
	}

	/**
	 * An operation that modifies the expansion state of the tree ridget.
	 */
	private static final class ExpansionCommand {
		/** An expansion modification */
		private final ExpansionState state;
		/** The element to expand / collapse (only for COLLAPSE, EXPAND ops) */
		private final Object element;

		/**
		 * Creates a new ExpansionCommand instance.
		 * 
		 * @param state
		 *            an expansion modification
		 * @param element
		 *            the element to expand / collapse (null for FULLY_EXPAND / FULLY_COLLAPSE)
		 */
		ExpansionCommand(final ExpansionState state, final Object element) {
			this.state = state;
			this.element = element;
		}
	}

	/**
	 * Disallows multiple selection is the selection type of the ridget is {@link ISelectableRidget.SelectionType#SINGLE}.
	 */
	private final class SelectionTypeEnforcer extends SelectionAdapter {
		@Override
		public void widgetSelected(final SelectionEvent e) {
			final Tree control = (Tree) e.widget;
			if (isOutputOnly()) {
				// ignore this event
				e.doit = false;
				restoreSelection();
			} else if (SelectionType.SINGLE.equals(getSelectionType())) {
				if (control.getSelectionCount() > 1) {
					// ignore this event
					e.doit = false;
					// set selection one item
					final TreeItem firstItem = control.getSelection()[0];
					control.setSelection(firstItem);
					// fire event
					final Event event = new Event();
					event.type = SWT.Selection;
					event.doit = true;
					control.notifyListeners(SWT.Selection, event);
				}
			}
		}
	}

	/**
	 * Notifies doubleClickListeners when the bound widget is double clicked.
	 */
	private final class DoubleClickForwarder extends MouseAdapter {
		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			if (doubleClickListeners != null) {
				for (final IActionListener listener : doubleClickListeners.getListeners()) {
					listener.callback();
				}
			}
		}
	}

	/**
	 * This class is used as the tree viewer's input when showRoots is false.
	 * <p>
	 * It uses reflection to obtain the current list of children from the real root of the model, while keeping the input element (i.e. this instance) int the
	 * tree all the time. This workaround allows us to update the level-0 of the tree without having to call setInput(...) on the tree viewer:
	 * 
	 * <pre>
	 * FakeRoot fakeRoot;
	 * viewer.setInput(...);
	 * // ... later ...
	 * fakeRoot.refresh();
	 * viewer.refresh(fakeRoot);
	 * </pre>
	 * 
	 * It uses reflection to obtain a n update list of children from the real root of the model.
	 * 
	 * @see TreeRidget#bindToModel(Object[], Class, String, String, String)
	 * @see TreeContentProvider
	 */
	static final class FakeRoot extends ArrayList<Object> {
		private static final long serialVersionUID = 1L;
		private final Object root0;
		private String accessor;

		FakeRoot(final Object root0, final String childrenAccessor) {
			Assert.isNotNull(childrenAccessor);
			clear();
			this.root0 = root0;
			if (root0 != null) {
				this.accessor = "get" + StringUtils.capitalize(childrenAccessor); //$NON-NLS-1$
				addAll(ReflectionUtils.<List<Object>> invoke(root0, accessor));
			}
		}

		Object getRoot() {
			return root0;
		}
	}

	/**
	 * Advisor class for the Eclipse 3.4 tree databinding framework. See {link TreeStructureAdvisor}.
	 * <p>
	 * This advisor uses the supplied property name and elementClass to invoke an appropriate accessor (get/isXXX method) on a element in the tree.
	 * <p>
	 * This functionality is used by the databinding framework to perform expand operations.
	 * 
	 * @see TreeStructureAdvisor
	 */
	private static final class GenericTreeStructureAdvisor extends TreeStructureAdvisor {

		private static final Object[] EMPTY_ARRAY = new Object[0];

		private final Class<?> beanClass;
		private PropertyDescriptor descriptor;

		GenericTreeStructureAdvisor(final String propertyName, final Class<?> elementClass) {
			Assert.isNotNull(propertyName);
			final String errorMsg = "propertyName cannot be empty"; //$NON-NLS-1$
			Assert.isLegal(propertyName.trim().length() > 0, errorMsg);
			Assert.isNotNull(elementClass);

			final String readMethodName = "get" + StringUtils.capitalize(propertyName); //$NON-NLS-1$
			try {
				descriptor = new PropertyDescriptor(propertyName, elementClass, readMethodName, null);
			} catch (final IntrospectionException exc) {
				log("Could not introspect bean.", exc); //$NON-NLS-1$
				descriptor = null;
			}
			this.beanClass = elementClass;
		}

		@Override
		public Object getParent(final Object element) {
			Object result = null;
			if (element != null && beanClass.isAssignableFrom(element.getClass()) && descriptor != null) {
				final Method readMethod = descriptor.getReadMethod();
				if (!readMethod.isAccessible()) {
					readMethod.setAccessible(true);
				}
				try {
					result = readMethod.invoke(element, EMPTY_ARRAY);
				} catch (final InvocationTargetException exc) {
					log("Error invoking.", exc); //$NON-NLS-1$
				} catch (final IllegalAccessException exc) {
					log("Error invoking.", exc); //$NON-NLS-1$
				}
			}
			return result;
		}

		private void log(final String message, final Exception exc) {
			final Logger logger = Log4r.getLogger(Activator.getDefault(), TreeRidget.class);
			logger.log(LogService.LOG_ERROR, message, exc);
		}
	}

	/**
	 * This change listener reacts to additions / removals of objects from the tree and is responsible for updating the image of the <b>parent</b> element.
	 * Specifically:
	 * <ul>
	 * <li>if B gets added to A we have to refresh the icon of A, if A did not have any children beforehand</li>
	 * <li>if B gets removed to A we have to refresh the icon of A, if B was the last child underneath A</li>
	 * <ul>
	 */
	private static final class TreeContentChangeListener implements ISetChangeListener {

		private final TreeViewer viewer;
		private final TreeStructureAdvisor structureAdvisor;

		private TreeContentChangeListener(final TreeViewer viewer, final TreeStructureAdvisor structureAdvisor) {
			Assert.isNotNull(structureAdvisor);
			this.structureAdvisor = structureAdvisor;
			this.viewer = viewer;
			Assert.isNotNull(viewer.getContentProvider());
		}

		/**
		 * Updates the icons of the parent elements on addition / removal
		 */
		public void handleSetChange(final SetChangeEvent event) {
			if (viewer.getLabelProvider(0) == null) {
				return;
			}
			final Set<Object> parents = new HashSet<Object>();
			for (final Object element : event.diff.getAdditions()) {
				final Object parent = structureAdvisor.getParent(element);
				if (parent != null) {
					parents.add(parent);
				}
			}
			for (final Object element : event.diff.getRemovals()) {
				final Object parent = structureAdvisor.getParent(element);
				if (parent != null) {
					parents.add(parent);
				}
			}
			for (final Object parent : parents) {
				if (!viewer.isBusy()) {
					viewer.update(parent, null);
				}
			}
		}
	}
}
