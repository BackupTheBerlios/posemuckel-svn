/**
 * 
 */
package posemuckel.client.gui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;

import posemuckel.client.gui.actions.OpenProjectAction;
import posemuckel.client.model.MemberList;
import posemuckel.client.model.Model;
import posemuckel.client.model.ProjectList;
import posemuckel.common.GetText;

/**
 * Ein ProjectTab enthält alle für eine Projektliste nötigen GUI-Elemente.
 * Neben einer Tabelle mit den Projekten ist ein Feld für die Projektbeschreibung
 * und eine Tabelle für die Mitglieder eines Projektes vorgesehen. Die 
 * Projektbeschreibung und die Mitgliederliste beziehen sich immer auf das selektierte
 * Projekt.
 * 
 * @author Posemuckel Team
 *
 */

public class ProjectTab extends MyTab {
	
	private TabItem tab;
	private GUI_Main_PublicProjects_Composite projectComposite;
	private Text text;
	private ProjectList projects;
	
	public ProjectTab(ProjectList projects) {
		this.projects = projects;
	}
	
	public void setEnabledOpenProject(boolean enabled) {
		if(projectComposite != null) {
			projectComposite.setEnabledOpenProject(enabled);
		}
	}
	
	public void createContent(TabFolder folder) {
		/*
		 * den Tab einbauen und mit einer SashForm ausstatten
		 */
		tab = getTab(folder, SWT.NONE);
		tab.setText(getTitle());
		SashForm mainContainer = new SashForm(folder, SWT.VERTICAL);
		tab.setControl(mainContainer);
		/*
		 * den Tab mit Inhalt füllen
		 */
		projectComposite=
			new GUI_Main_PublicProjects_Composite(mainContainer, projects);
		SashForm innerContainer = new SashForm(mainContainer, SWT.HORIZONTAL);
		GUI_MemberList_Composite participants = new GUI_MemberList_Composite(innerContainer, MemberList.getDummyList(MemberList.PROJECT, Model.getModel()));
		projectComposite.setParticipantsWidget(participants);
		addTextComposite(innerContainer);
		
		mainContainer.setWeights(new int[]{3, 2});
		innerContainer.setWeights(new int[]{1, 1});
	}
	
	private void addTextComposite(Composite parent) {
		Composite textComp = new Composite(parent, SWT.NULL);
		textComp.setLayout(MyLayoutFactory.createGrid(1, false, 2));
		Label label = new Label(textComp, SWT.SHADOW_NONE);
		GridData data1 = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		data1.verticalSpan = 1;
		data1.horizontalSpan = 1;
		label.setLayoutData(data1);
		label.setText(GetText.gettext("PROJECT_DESCRIPTION"));
		text = new Text(textComp, SWT.MULTI |SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		GridData data = new GridData( GridData.FILL_BOTH );
		text.setLayoutData(data);
		text.setEditable(false);
		projectComposite.setDescriptionWidget(text);
	}
	
	private String getTitle() {
		if(projects.getType().equals(ProjectList.MY_PROJECTS)) {
			return GetText.gettext("MY_PROJECTS");	
		} else if (projects.getType().equals(ProjectList.OPEN_INVITATIONS)){
			return GetText.gettext("INVITATIONS");	
		} else {
			return GetText.gettext("OTHER_PROJECTS");	
		}
	}
	
	protected void initOpenAction(OpenProjectAction action) {
		projectComposite.initOpenAction(action);
	}


}
