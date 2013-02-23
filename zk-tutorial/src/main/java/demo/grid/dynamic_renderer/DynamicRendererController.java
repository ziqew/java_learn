package demo.grid.dynamic_renderer;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelList;

import demo.data.ContributorData;
import demo.data.pojo.LanguageContribution;

public class DynamicRendererController extends SelectorComposer<Component> {
    private static final long serialVersionUID = 1L;

    private final ListModel<LanguageContribution> langContributors =
            new ListModelList<LanguageContribution>(new ContributorData().getLanguageContributors());

    public ListModel<LanguageContribution> getContributors() {
        return langContributors;
    }
}