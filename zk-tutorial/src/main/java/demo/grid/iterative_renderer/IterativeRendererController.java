package demo.grid.iterative_renderer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zk.ui.select.annotation.Listen;
import org.zkoss.zul.Button;
import org.zkoss.zul.Label;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import demo.data.ContributorData;
import demo.data.pojo.LanguageContribution;

public class IterativeRendererController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    ListModel<LanguageContribution> languageContributors =
            new ListModelList<LanguageContribution>(new ContributorData().getLanguageContributors());

    public ListModel<LanguageContribution> getLanguageContributors() {
        return languageContributors;
    }

    @Listen("onClick = button")
    public void doThumbUp(Event event){
        Button btn = (Button)event.getTarget();
        btn.getParent().appendChild(new Label("Thumbs Up!!!"));
        btn.setDisabled(true);
    }
}