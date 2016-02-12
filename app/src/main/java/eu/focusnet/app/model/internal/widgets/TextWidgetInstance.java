package eu.focusnet.app.model.internal.widgets;

import java.util.Map;

import eu.focusnet.app.model.json.WidgetTemplate;
import eu.focusnet.app.model.internal.DataContext;
import eu.focusnet.app.model.util.TypesHelper;

/**
 * Created by julien on 20.01.16.
 */
public class TextWidgetInstance extends WidgetInstance
{

	private static final String CONFIG_LABEL_CONTENT = "content";

	private String content;

	public TextWidgetInstance(WidgetTemplate template, Map<String, String> layoutConfig, DataContext newCtx)
	{
		super(template, layoutConfig, newCtx);
	}

	/**
	 * A TextWidgetInstance defines:
	 * - title (String)
	 * - content (String)
	 */
	@Override
	void processConfig() // FIXME use TypesHelper
	{
		this.content = TypesHelper.asString(this.config.get(CONFIG_LABEL_CONTENT));
		return;
	}


	/**
	 * Get the content
	 *
	 * @return
	 */
	public String getContent()
	{
		return content;
	}
}
