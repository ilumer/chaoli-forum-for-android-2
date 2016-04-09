package com.geno.chaoli.forum;

import java.lang.reflect.Field;

@SuppressWarnings("unused")
public final class TODO
{
	public static final class BASIC
	{
		public static final boolean VIEW_CONVERSATION_LIST = true;
		@Lazy
		public static final boolean VIEW_CONVERSATION_LIST_SCROLL = false;
		public static final boolean LOGIN = true;
		public static final boolean NEW_CONVERSATION = true;
		public static final boolean REPLY = true;
		public static final boolean DELETE = true;
		@Lazy
		public static final boolean RECOVER = false;
		public static final boolean VIEW_POST = true;
		@Lazy
		public static final boolean VIEW_POST_SCROLL = false;
		public static final boolean USER_PAGE = true;
	}

	public static final class IMPLE
	{
		public static final boolean FORMAT_POST_VIEW_OUTPUT = false;
		public static final boolean MATH_JAX_RENDER = false;
		public static final boolean LATEX_RENDER = false;
	}

	public static final class ISSUE
	{
		public static final boolean FINISH = false;
	}

	public static final class LATER
	{
		public static final boolean FINISH = false;
	}

	public static double getStatus()
	{
		double res = 0;
		int count = 0;
		try
		{
			for(Class<?> c : Class.forName("com.geno.chaoli.forum.TODO").getClasses())
			{
				for (Field f : c.getFields())
				{
					res += f.getBoolean("") ? 1 : 0;
					count++;
				}
			}
		}
		catch (Exception e)
		{e.printStackTrace();}
		return res/count;
	}

}
