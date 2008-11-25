/*******************************************************************************
 * Copyright (c) 2007, 2008 compeople AG and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    compeople AG - initial API and implementation
 *******************************************************************************/
package org.eclipse.riena.demo.customer.client.controllers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.riena.core.injector.Inject;
import org.eclipse.riena.demo.customer.client.model.SearchResultContainer;
import org.eclipse.riena.demo.customer.common.CustomerRecordOverview;
import org.eclipse.riena.demo.customer.common.CustomerSearchBean;
import org.eclipse.riena.demo.customer.common.CustomerSearchResult;
import org.eclipse.riena.demo.customer.common.ICustomerDemoService;
import org.eclipse.riena.internal.demo.customer.client.Activator;
import org.eclipse.riena.navigation.NavigationNodeId;
import org.eclipse.riena.navigation.ui.controllers.SubModuleController;
import org.eclipse.riena.ui.ridgets.IActionListener;
import org.eclipse.riena.ui.ridgets.IActionRidget;
import org.eclipse.riena.ui.ridgets.ILabelRidget;
import org.eclipse.riena.ui.ridgets.IRidget;
import org.eclipse.riena.ui.ridgets.ITableRidget;
import org.eclipse.riena.ui.ridgets.ITextRidget;
import org.eclipse.riena.ui.ridgets.ISelectableRidget.SelectionType;

/**
 *
 */
public class KundenSucheController extends SubModuleController {
	private CustomerSearchBean customerSearchBean = new CustomerSearchBean();
	private CustomerSearchResult ergebnis;
	private ICustomerDemoService customerDemoService;

	public void bind(ICustomerDemoService customerDemoService) {
		this.customerDemoService = customerDemoService;
	}

	public void unbind(ICustomerDemoService customerDemoService) {
		this.customerDemoService = null;
	}

	@Override
	public void configureRidgets() {
		Inject.service(ICustomerDemoService.class).into(this).andStart(Activator.getDefault().getBundle().getBundleContext());

		ITextRidget suchName = (ITextRidget) getRidget("suchName");
		suchName.bindToModel(customerSearchBean, "lastName");
		suchName.setMandatory(true);

		((ITextRidget) getRidget("suchVorname")).bindToModel(customerSearchBean, "firstName");
		((ITextRidget) getRidget("suchPlz")).bindToModel(customerSearchBean, "zipcode");
		((ITextRidget) getRidget("suchOrt")).bindToModel(customerSearchBean, "city");
		((ITextRidget) getRidget("suchStrasse")).bindToModel(customerSearchBean, "street");
		// ((ILabelRidget) getRidget("treffer")).bindToModel(customerSearchBean,
		// "treffer");

		ITableRidget kunden = ((ITableRidget) getRidget("ergebnis"));
		String[] columnNames = { "lastname", "firstname", "custno.", "birthdate", "street", "zip", "city", "status", "salesrep", "phone" };
		String[] propertyNames = { "lastName", "firstName", "customerNumber", "birthdate", "street", "zipcode", "city", "status", "salesrepno",
				"telefoneNumber" };
		final SearchResultContainer searchResultContainer = new SearchResultContainer();
		kunden.bindToModel(searchResultContainer, "customerList", CustomerRecordOverview.class, propertyNames, columnNames);

		((IActionRidget) getRidget("reset")).addListener(new IActionListener() {

			public void callback() {
				// ergebnis = getPersonenService().suche(getSuchBean());
				getNavigationNode().navigate(new NavigationNodeId("org.eclipse.riena.demo.client.module.CustomerRecord"));
			}
		});
		((IActionRidget) getRidget("search")).addListener(new IActionListener() {

			public void callback() {
				searchResultContainer.setCustomerList(null);
				((IRidget) getRidget("ergebnis")).updateFromModel();
				ergebnis = customerDemoService.suche(getSuchPerson());
				// List<PersonenSucheErgebnisBean> kundenRows = new
				// ArrayList<PersonenSucheErgebnisBean>();
				if (ergebnis == null || ergebnis.getFehler()) {
					((ILabelRidget) getRidget("treffer")).setText("Keine Treffer");
					((IRidget) getRidget("treffer")).updateFromModel();
					return;
				}
				((ILabelRidget) getRidget("treffer")).setText(ergebnis.getErgebnismenge() + " Treffer");
				((IRidget) getRidget("treffer")).updateFromModel();
				// transformSuchergebnis(ergebnis, kundenRows);
				List<CustomerRecordOverview> result = new ArrayList<CustomerRecordOverview>();
				for (CustomerRecordOverview cust : ergebnis.getErgebnis()) {
					result.add(cust);
				}
				searchResultContainer.setCustomerList(result);
				((IRidget) getRidget("ergebnis")).updateFromModel();
				// getAktenSucheBean().getSelection().clear();
				// if (kundenRows.size() > 0)
				// kunden.setSelection(kundenRows.get(0));
				// kunden.updateMultiSelectionFromModel();
				// setDefaultButton(oeffnenAction);
				// ((JTable) kunden.getUIControl()).requestFocusInWindow();
			}
		});
	}

	// private void transformSuchergebnis(CustomerSearchResult ergebnis,
	// List<PersonenSucheErgebnisBean> ergebnisListe) {
	// int treffer = ergebnis.getErgebnis() == null ? 0 :
	// ergebnis.getErgebnis().length;
	// if (treffer > 0) {
	// for (int i = 0; i < treffer; i++) {
	// IPersonenAkteUebersicht personenAkteUebersicht =
	// ergebnis.getErgebnis()[i];
	// PersonenSucheErgebnisBean kundenSucheErgebnisBean = new
	// PersonenSucheErgebnisBean();
	// if (personenAkteUebersicht.getKundenNummer() != null) {
	// kundenSucheErgebnisBean.setKundenNummer(Long.toString(
	// personenAkteUebersicht.getKundenNummer().intValue()));
	// }
	// kundenSucheErgebnisBean.setNachName(personenAkteUebersicht.getName());
	// kundenSucheErgebnisBean.setStrasse(personenAkteUebersicht.getStrasse());
	// kundenSucheErgebnisBean.setPlz(personenAkteUebersicht.getPlz());
	// kundenSucheErgebnisBean.setOrt(personenAkteUebersicht.getOrt());
	// if (personenAkteUebersicht.getStatus() != null) {
	// kundenSucheErgebnisBean.setStatus(personenAkteUebersicht.getStatus().
	// getValue());
	// }
	// if (personenAkteUebersicht.getVbNummer() != null) {
	//kundenSucheErgebnisBean.setVbNummer(Integer.toString(personenAkteUebersicht
	// .getVbNummer().intValue()));
	// }
	// // kundenSucheErgebnisBean.setMehrfachBetreuung(
	// // personenAkteUebersicht.getMehrfachBetreuung());
	// // kundenSucheErgebnisBean.setKommissarisch(false);
	// kundenSucheErgebnisBean.setRufNummer(personenAkteUebersicht.
	// getTelefonNummer());
	// kundenSucheErgebnisBean.setVorName(personenAkteUebersicht.getVorname());
	// // kundenSucheErgebnisBean.setAnrede(personenAkteUebersicht.
	// // getAnrede());
	// if (personenAkteUebersicht.getGeburtsdatum() != null) {
	// kundenSucheErgebnisBean.setGeburtsdatum(personenAkteUebersicht.
	// getGeburtsdatum().toString());
	// }
	// // kundenSucheErgebnisBean.setMandant(personenAkteUebersicht.
	// // getMandant());
	// //kundenSucheErgebnisBean.setUebersicht(personenAkteUebersicht);
	// ergebnisListe.add(kundenSucheErgebnisBean);
	// }
	// Collections.sort(ergebnisListe);
	// } else {
	// if (ergebnis.getFehler())
	// JOptionPane.showMessageDialog(null, "Fehler bei Suche", "Achtung Fehler",
	// JOptionPane.ERROR_MESSAGE);
	// else
	// JOptionPane.showMessageDialog(null, "Suche ergab keine Treffer",
	// "Achtung", JOptionPane.WARNING_MESSAGE);
	// }
	// }

	public CustomerSearchBean getSuchPerson() {
		return customerSearchBean;
	}

	// public void bind(IPerson)

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.riena.navigation.ui.controllers.SubModuleController#afterBind
	 * ()
	 */
	@Override
	public void afterBind() {
		super.afterBind();

		ITableRidget kunden = ((ITableRidget) getRidget("ergebnis"));
		kunden.setSelectionType(SelectionType.MULTI);
		for (int i = 0; i < 9; i++) {
			kunden.setColumnSortable(i, true);
		}
	}

	// class SuchBean {
	//
	// private String suchName;
	// private String suchVorname;
	// private String suchPlz;
	// private String suchOrt;
	// private String suchStrasse;
	// private String suchTreffer;
	//
	// public String getSuchName() {
	// return suchName;
	// }
	//
	// public void setSuchName(String suchName) {
	// this.suchName = suchName;
	// }
	//
	// public String getSuchVorname() {
	// return suchVorname;
	// }
	//
	// public void setSuchVorname(String suchVorname) {
	// this.suchVorname = suchVorname;
	// }
	//
	// public String getSuchPlz() {
	// return suchPlz;
	// }
	//
	// public void setSuchPlz(String suchPlz) {
	// this.suchPlz = suchPlz;
	// }
	//
	// public String getSuchOrt() {
	// return suchOrt;
	// }
	//
	// public void setSuchOrt(String suchOrt) {
	// this.suchOrt = suchOrt;
	// }
	//
	// public String getSuchTreffer() {
	// return suchTreffer;
	// }
	//
	// public void setSuchTreffer(String suchTreffer) {
	// this.suchTreffer = suchTreffer;
	// }
	//
	// public String getSuchStrasse() {
	// return suchStrasse;
	// }
	//
	// public void setSuchStrasse(String suchStrasse) {
	// this.suchStrasse = suchStrasse;
	// }
	//
	// }

}
