package eu.focusnet.app.model.json;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by admin on 03.08.2015.
 */
public class AppContentTemplate extends FocusObject
{

	private Long id;
	private LinkedHashMap<String, String> data;
	private ArrayList<ProjectTemplate> projects;

	public AppContentTemplate(String type, String url, String context, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active, ArrayList<ProjectTemplate> projects, LinkedHashMap<String, String> data)
	{
		super(type, url, context, owner, editor, version, creationDateTime, editionDateTime, active);
		this.projects = projects;
		this.data = data;
	}

	public AppContentTemplate(Long id, ArrayList<ProjectTemplate> projects, LinkedHashMap<String, String> data)
	{
		this.id = id;
		this.projects = projects;
		this.data = data;
	}

	public AppContentTemplate()
	{
	}

	public Long getId()
	{
		return id;
	}

	public void setId(Long id)
	{
		this.id = id;
	}

	public ArrayList<ProjectTemplate> getProjects()
	{
		return projects;
	}

	public void setProjects(ArrayList<ProjectTemplate> projects)
	{
		this.projects = projects;
	}

	public HashMap<String, String> getData()
	{
		return this.data;
	}

}
