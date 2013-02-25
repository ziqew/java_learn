package demo.grid.dynamic_renderer;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Label;
import org.zkoss.zul.Row;
import org.zkoss.zul.RowRenderer;

import demo.data.pojo.LanguageContribution;

public class SimpleRenderer implements RowRenderer<LanguageContribution> {

    public void render(Row row, LanguageContribution data, int index) {
        // the data append to each row with simple label
        row.appendChild(new Label(Integer.toString(index)));
        row.appendChild(new Label(data.getLanguage()));
        row.appendChild(new Label(data.getName()));
        row.appendChild(new Label(data.getCharset()));
        // we create a thumb up/down comment to each row
        final Div d = new Div();
        final Button thumbBtn = new Button(null, "/images/thumb-up.png");
        thumbBtn.setParent(d);
        thumbBtn.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
            public void onEvent(Event event) throws Exception {
                d.appendChild(new Label("Thumbs up"));
                thumbBtn.setDisabled(true);
            }
        });
        row.appendChild(d); // any component could created as a child of grid
    }
}