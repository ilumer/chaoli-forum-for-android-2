package com.geno.chaoli.forum.meta;

public enum Channel
{
	// TODO: 2016/2/4 0230 Use R.string to get instead of hardcode.
	caff("茶馆", "灌水、闲聊、漫游", 1, false),
	maths("数学", "眼前是无穷的数学海洋，准备好启程了吗？", 4, true),
	physics("物理", "能量与动量齐飞，时间共空间一色", 5, true),
	chem("化学", "化学相关的各种学术、各种各样化学实验！", 6, true),
	biology("生物", "从生物分子到生态系统的生命科学集锦", 7, true),
	tech("技术", "你为什么不问问神奇海螺呢？", 8, true),
	test("公测", "试验功能、报告问题", 9, false),
	others("其他", "语言、社科", 30, true),
	socsci("社科", "", 34, true),
	lang("语言", "", 40, true);

	private String name;
	private String detail;
	private boolean isGuestVisible;
	private int channelId;

	Channel(String name, String detail, int channelId, boolean isGuestVisible)
	{
		this.name = name;
		this.detail = detail;
		this.channelId = channelId;
		this.isGuestVisible = isGuestVisible;
	}

	public String getDetail()
	{
		return this.detail;
	}

	public int getChannelId()
	{
		return channelId;
	}

	// FIXME: 2016/2/4 0252 A function that will never be used.
	public boolean isGuestVisible()
	{
		return isGuestVisible;
	}
	
	@Override
	public String toString()
	{
		return this.name;
	}
}
