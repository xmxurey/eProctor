package eProctor;

import javax.swing.*;

public class FunctionButton extends JButton{
	private String moduleCode;
	private String label;
	
	public FunctionButton(String l, String m){
		super(l);
		moduleCode = m;
	}
	public String getModuleCode(){
		return moduleCode;
	}
	public void setModuleCode(String m){
		moduleCode = m;
	}
}