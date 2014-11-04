import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
 
@Agent //Identifica a classe como sendo um agente
@Description("An agent that says hello to the world.") //Fornece descrição do agente que surge no JCC
public class HelloWorldAgent {
 
	@AgentBody //especifica qual é o método a ser invocado lgoo após a criação do agente
	public void sayHello(){
		System.out.println("Hello word!");
	}
 
}