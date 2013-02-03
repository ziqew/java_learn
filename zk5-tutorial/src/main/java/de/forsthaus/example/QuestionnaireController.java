package de.forsthaus.example;

import org.zkoss.zk.ui.util.GenericForwardComposer;

public class QuestionnaireController extends GenericForwardComposer {
//	/**
//	 * 
//	 */
//	private static final long serialVersionUID = 1L;
//	//AnswerDataDAO ansdao = new AnswerDataDAO();
////	QuestionDAO quesdao = new QuestionDAO();
//	PersonDAO persdao = new PersonDAOImpl();
////	OrgUnitDAO orgudao = new OrgUnitDAO();
////	AnswerData answers[] = new AnswerData[1000];
////	Question currentQ = new Question();
//	Person currentP = new Person();
//	Tabbox tBox;
//	Tabpanel personal,quest1,quest2;
//	Button btnSubmit,btnRegister,btnProceed;
//	Listbox lBox,lBox2; 
//	Label orderId,orderId2,lblId,lblId2;
//	Label system;
//	Combobox pOrgU;
//	
//	public void doAfterCompose(Component comp) 
//	{
//		String uName = System.getProperty("user.name");
//		try {
//			super.doAfterCompose(comp);
//		} 
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		lBox.setVisible(false);
//		lBox2.setVisible(false);
//		btnProceed.setVisible(false);
//		btnSubmit.setVisible(false);
//		((Textbox)personal.getFellow("pUserName")).setValue(uName);
//		currentP = persdao.getPerson(uName);
//		
//		if (currentP != null)
//			updateFields();
//	}
//	
//	public void onCreate$qWin()
//	{
//		List<Comboitem> comboList;
//		//alert("bla");
//		comboList= pOrgU.getChildren();
//		//alert(comboList.toString());
//		//returns nothing 
//	}
//	
//	private void updateFields() 
//	{
//		Iterator<Comboitem> itr;
//		List<Comboitem> comboList;
//		Comboitem cmbItm;
//
//		comboList= ((Combobox)personal.getFellow("pOrgU")).getItems();
//		cmbItm=(Comboitem) ((Combobox)personal.getFellow("pOrgU")).getFirstChild();
//		//returns nothing again....
//		//alert(cmbItm.toString());
//		itr= comboList.iterator();
//
//		/*
//		while(itr.hasNext())
//		{
//			Comboitem cur = itr.next();
//			//alert(cur.toString());
//			//if (Integer.parseInt(""+cur.getValue()) == currentP.getOrg_unit())
//			//	((Combobox)personal.getFellow("pOrgU")).setSelectedItem(cur);
//		}
//		*/
//		//alert("Hi 2");
//		((Textbox)personal.getFellow("pAge")).setValue(""+currentP.getAge());
//		
//		comboList = ((Combobox)personal.getFellow("pGender")).getItems();
//		itr= comboList.iterator();
//		while(itr.hasNext())
//		{
//			Comboitem cur = itr.next();
//			if (Byte.parseByte(""+cur.getValue()) == currentP.getGender())
//				((Combobox)personal.getFellow("pGender")).setSelectedItem(cur);
//		}		
//		
//		((Textbox)personal.getFellow("pProfSenior")).setValue(""+currentP.getProf_senior());
//		((Textbox)personal.getFellow("pWorkSenior")).setValue(""+currentP.getWorkplace_senior());
//		
//		comboList = ((Combobox)personal.getFellow("pManStatus")).getItems();
//		itr= comboList.iterator();
//		while(itr.hasNext())
//		{
//			Comboitem cur = itr.next();
//			if (Byte.parseByte(""+cur.getValue()) == currentP.getManage_status())
//				((Combobox)personal.getFellow("pManStatus")).setSelectedItem(cur);
//		}		
//		
//		comboList = ((Combobox)personal.getFellow("pEducation")).getItems();
//		itr= comboList.iterator();
//		while(itr.hasNext())
//		{
//			Comboitem cur = itr.next();
//			if (Byte.parseByte(""+cur.getValue()) == currentP.getEducation())
//				((Combobox)personal.getFellow("pEducation")).setSelectedItem(cur);
//		}		
//	
//		comboList = ((Combobox)personal.getFellow("pBArea")).getItems();
//		itr= comboList.iterator();
//		while(itr.hasNext())
//		{
//			Comboitem cur = itr.next();
//			if (Byte.parseByte(""+cur.getValue()) == currentP.getBusiness_area())
//				((Combobox)personal.getFellow("pBArea")).setSelectedItem(cur);
//		}		
//	}
//
//	public Question getCurrentQ() {
//		return currentQ;
//	}
//	public void setCurrentQ(Question current) {
//		this.currentQ = current;
//	}
//	public List<OrgUnit> getAllOrgUnits() {
//		return orgudao.getAll();
//	}	
//	//All Type 1 Questions
//	public List<Question> getAllQuestions1() {
//		return quesdao.getAll(1);
//	}	
//	//All Type 2 Questions
//	public List<Question> getAllQuestions2() {
//		return quesdao.getAll(2);
//	}	
//	
//	public void onClick$btnRegister()
//	{
//		String ret=allFieldsFilled();
//		long id=0;
//		if (ret.equals("AllGood"))
//		{
//			//Register person
//			Person newPers = new Person(((Textbox)personal.getFellow("pUserName")).getValue(),
//					Integer.parseInt(""+((Combobox)personal.getFellow("pOrgU")).getSelectedItem().getValue()),
//					Integer.parseInt(((Textbox)personal.getFellow("pAge")).getValue()),
//					Byte.parseByte(""+((Combobox)personal.getFellow("pGender")).getSelectedItem().getValue()),
//					Byte.parseByte(((Textbox)personal.getFellow("pProfSenior")).getValue()),
//					Byte.parseByte(((Textbox)personal.getFellow("pWorkSenior")).getValue()),
//					Byte.parseByte(""+((Combobox)personal.getFellow("pManStatus")).getSelectedItem().getValue()),
//					Byte.parseByte(""+((Combobox)personal.getFellow("pEducation")).getSelectedItem().getValue()),
//					Byte.parseByte(""+((Combobox)personal.getFellow("pBArea")).getSelectedItem().getValue()),
//					new Date());
//			id=persdao.insert(newPers);
//			if(id==0)
//			{
//				try {
//					Messagebox.show("User already filled questionnaire","Error",Messagebox.OK,Messagebox.ERROR);
//				} catch (ComponentNotFoundException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				return ;
//			}
//			else if (id<0)
//			{
//				try {
//					Messagebox.show("Details were updated successfully \n Please fill 'Questionnaire Part I'","Confirmation",Messagebox.OK,Messagebox.INFORMATION);
//				} catch (ComponentNotFoundException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			else 
//			{
//				try {
//					Messagebox.show("Details were successfully recorded \n Please proceed to questionnaire","Confirmation",Messagebox.OK,Messagebox.INFORMATION);
//				} catch (ComponentNotFoundException e) {
//					e.printStackTrace();
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//			}
//			
//			currentP = persdao.getPerson(newPers.getUser_Name());
//			lBox.setVisible(true);
//			lblId.setVisible(false);
//			tBox.setSelectedPanel(quest1);
//			btnProceed.setVisible(true);
//		}
//		else
//		{
//			try {
//				Messagebox.show("Please fill in your " +ret+ " before you continue with registration", "Missing details", Messagebox.OK, Messagebox.EXCLAMATION);
//			} catch (ComponentNotFoundException e) {
//				e.printStackTrace();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//	
//	private String allFieldsFilled() 
//	{	
//		if (((Combobox)personal.getFellow("pOrgU")).getSelectedItem()==null) return "Organizational Unit";
//		else if (((Combobox)personal.getFellow("pGender")).getSelectedItem()==null) return "Gender";
//		else if (((Textbox)personal.getFellow("pAge")).getValue().equals("")) return "Age";
//		else if (((Textbox)personal.getFellow("pProfSenior")).getValue().equals("")) return "Professional Seniority";
//		else if (((Textbox)personal.getFellow("pWorkSenior")).getValue().equals("")) return "Workplace Seniority";
//		else if (((Combobox)personal.getFellow("pManStatus")).getSelectedItem()==null) return "Managerial Status";
//		else if (((Combobox)personal.getFellow("pEducation")).getSelectedItem()==null) return "Education";
//		else if (((Combobox)personal.getFellow("pBArea")).getSelectedItem()==null) return "Business Area";
//		return "AllGood";
//	}
//
//	public void onCheckAnswer(int answerId, int oId, Listbox lbox) 
//	{
//		Listcell lc,lc2;
//		//int oId = Integer.parseInt(orderId.getValue());
//		lbox.setSelectedIndex(oId-1);
//		lc=(Listcell) lbox.getSelectedItem().getChildren().get(0);
//		lc2=(Listcell) lbox.getSelectedItem().getChildren().get(2);
//		int qId = Integer.parseInt(lc.getValue().toString());
//
//		AnswerData answerD = new AnswerData(Long.toString(currentP.getId()),qId,answerId);
//		system.setValue(answerD.toString());
//		answers=answerD;
//		((Label) lc2.getChildren().get(3)).setValue("");
//	}
//	
//	public void onCheck$ans1_1() { onCheckAnswer(1,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_2() { onCheckAnswer(2,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_3() { onCheckAnswer(3,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_4() { onCheckAnswer(4,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_5() { onCheckAnswer(5,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_6() { onCheckAnswer(6,Integer.parseInt(orderId.getValue()),lBox);}
//	public void onCheck$ans1_7() { onCheckAnswer(7,Integer.parseInt(orderId.getValue()),lBox);}
//	
//	public void onCheck$ans2_1() { onCheckAnswer(1,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_2() { onCheckAnswer(2,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_3() { onCheckAnswer(3,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_4() { onCheckAnswer(4,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_5() { onCheckAnswer(5,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_6() { onCheckAnswer(6,Integer.parseInt(orderId2.getValue()),lBox2);}
//	public void onCheck$ans2_7() { onCheckAnswer(7,Integer.parseInt(orderId2.getValue()),lBox2);}
//	
//	public void onClick$btnProceed()
//	{
//		Iterator<Listitem> itr;
//		List<Listitem> listCell;
//		Listitem curItm;
//		Label curLbl;
//		
//		listCell = lBox.getItems();
//		itr = listCell.iterator();
//		
//		while(itr.hasNext())
//		{
//			curItm=itr.next();
//			curLbl=(Label) ((Listcell) curItm.getChildren().get(2)).getChildren().get(3);
//			if(curLbl.getValue().equals("*"))
//			{
//				//There are more questions to fill
//				lBox.setSelectedItem(curItm);
//				//curItm.setFocus(true);
//				return;
//			}
//		}
//		//in case all questions are filled - can proceed to next questionnaire
//		lblId2.setVisible(false);
//		lBox2.setVisible(true);
//		btnSubmit.setVisible(true);
//		tBox.setSelectedPanel(quest2);
//	}
//	
//	public void onClick$btnSubmit() 
//	{		
//		Iterator<Listitem> itr;
//		List<Listitem> listCell;
//		Listitem curItm;
//		Label curLbl;
//		int answer=Messagebox.NO;
//		
//		listCell = lBox2.getItems();
//		itr = listCell.iterator();
//		
//		while(itr.hasNext())
//		{
//			curItm=itr.next();
//			curLbl=(Label) ((Listcell) curItm.getChildren().get(2)).getChildren().get(3);
//			
//			if(curLbl.getValue().equals("*"))
//			{
//				//There are more questions to fill
//				lBox2.setSelectedItem(curItm);
//				return;
//			}
//		}
//		try {
//			answer=Messagebox.show("Are you sure you want to submit the questionnaire?", "Confirm submission",
//					Messagebox.YES | Messagebox.NO, Messagebox.QUESTION);
//		} catch (ComponentNotFoundException e) {
//			e.printStackTrace();
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		alert(""+answer + " " +Messagebox.NO);
//		if (answer==Messagebox.NO)
//			return;
//		else
//		{
//			//save answers to DB
//			alert("Submitting");
//		}
//		//in case all questions are filled - can proceed to next questionnaire
//	}	
}
