package eu.focusnet.app.model.focus;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by admin on 03.08.2015.
 */
public class AppContentTemplate extends FocusObject
{

	private Long id;
	private ArrayList<ProjectTemplate> projects;

	public AppContentTemplate(String type, String url, String owner, String editor, int version, Date creationDateTime, Date editionDateTime, boolean active, ArrayList<ProjectTemplate> projects)
	{
		super(type, url, owner, editor, version, creationDateTime, editionDateTime, active);
		this.projects = projects;
	}

	public AppContentTemplate(Long id, ArrayList<ProjectTemplate> projects)
	{
		this.id = id;
		this.projects = projects;
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



}
