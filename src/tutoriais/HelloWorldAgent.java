package tutoriais;

import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
 
@Agent //Identifica a classe como sendo um agente
@Description("An agent that says hello to the world.") //Fornece descri��o do agente que surge no JCC
public class HelloWorldAgent {
 
	@AgentBody //especifica qual � o m�todo a ser invocado lgoo ap�s a cria��o do agente
	public void sayHello(){
		System.out.println("Hello word!");
	}
 
}