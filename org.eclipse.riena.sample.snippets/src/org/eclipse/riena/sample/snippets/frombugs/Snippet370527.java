package org.eclipse.riena.sample.snippets.frombugs;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import org.eclipse.riena.ui.ridgets.swt.SwtRidgetFactory;
import org.eclipse.riena.ui.swt.utils.UIControlsFactory;

public class Snippet370527 {

	public static void main(final String[] args) {
		final Display display = Display.getDefault();
		try {
			final Shell shell = new Shell();
			shell.setLayout(new GridLayout());

			final Text text = UIControlsFactory.createTextDecimal(shell);
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			SwtRidgetFactory.createRidget(text);

			shell.setSize(400, 400);
			shell.open();
			while (!shell.isDisposed()) {
				if (!display.readAndDispatch()) {
					display.sleep();
				}
			}
		} finally {
			display.dispose();
		}
	}

}