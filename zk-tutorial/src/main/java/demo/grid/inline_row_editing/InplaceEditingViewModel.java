package demo.grid.inline_row_editing;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.bind.BindUtils;
import org.zkoss.bind.annotation.BindingParam;
import org.zkoss.bind.annotation.Command;
import org.zkoss.bind.annotation.NotifyChange;

import demo.data.ContributorData;
import demo.data.pojo.LanguageContribution;

public class InplaceEditingViewModel {
    private final ContributorData contributorData = new ContributorData();
    private final List<LanguageContributionStatus> contributionStatuses =
            generateStatusList(contributorData.getLanguageContributors());
    private boolean displayEdit = true;

    public boolean isDisplayEdit() {
        return displayEdit;
    }

    @NotifyChange({"languageContributions", "displayEdit"})
    public void setDisplayEdit(boolean displayEdit) {
        this.displayEdit = displayEdit;
    }

    public List<LanguageContributionStatus> getLanguageContributions() {
        return contributionStatuses;
    }

    @Command
    public void changeEditableStatus(@BindingParam("languageContributionStatus") LanguageContributionStatus lcs) {
        lcs.setEditingStatus(!lcs.getEditingStatus());
        refreshRowTemplate(lcs);
    }

    @Command
    public void confirm(@BindingParam("languageContributionStatus") LanguageContributionStatus lcs) {
        changeEditableStatus(lcs);
        refreshRowTemplate(lcs);
    }

    public void refreshRowTemplate(LanguageContributionStatus lcs) {
        /*
         * This code is special and notifies ZK that the bean's value
         * has changed as it is used in the template mechanism.
         * This stops the entire Grid's data from being refreshed
         */
        BindUtils.postNotifyChange(null, null, lcs, "editingStatus");
    }


    private static List<LanguageContributionStatus> generateStatusList(List<LanguageContribution> contributions) {
        List<LanguageContributionStatus> contribs = new ArrayList<LanguageContributionStatus>();
        for(LanguageContribution lc : contributions) {
            contribs.add(new LanguageContributionStatus(lc, false));
        }
        return contribs;
    }
}