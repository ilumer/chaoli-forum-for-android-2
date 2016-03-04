package com.geno.chaoli.forum.meta;

public enum Channel
{
	// TODO: 2016/2/4 0230 Use R.string to get instead of hardcode.
	caff("茶馆", "灌水、闲聊、漫游", 1, false, 0xFFA0A0A0),
	ad("店铺", "为赞助商预留", 3, false, 0xFF999999),
	maths("数学", "眼前是无穷的数学海洋，准备好启程了吗？", 4, true, 0xFF4030A0),
	physics("物理", "能量与动量齐飞，时间共空间一色", 5, true, 0xFFA00020),
	chem("化学", "化学相关的各种学术、各种各样化学实验！", 6, true, 0xFFE04000),
	biology("生物", "从生物分子到生态系统的生命科学集锦", 7, true, 0xFF008000),
	tech("技术", "你为什么不问问神奇海螺呢？", 8, true, 0xFF0040D0),
	test("公测", "试验功能、报告问题", 9, false, 0xFF202020),
	admin("管理", "超理论坛中央政府", 24, false, 0xFFEAEAEA),
	court("申诉", "运维人员对用户作出的处罚；对运维人员所作出决定的申诉。", 25, true, 0xFFE040D0),
	announ("公告", "论坛事务公告", 28, true, 0xFF999999),
	others("其他", "语言、社科", 30, true, 0xFFA0A0A0),
	socsci("社科", "社会科学", 34, true, 0xFFE04000),
	lang("语言", "语言学习交流", 40, true, 0xFF9030C0);

	private String name;
	private String detail;
	private boolean isGuestVisible;
	private int channelId;
	private int color;

	Channel(String name, String detail, int channelId, boolean isGuestVisible, int color)
	{
		this.name = name;
		this.detail = detail;
		this.channelId = channelId;
		this.isGuestVisible = isGuestVisible;
		this.color = color;
	}

	public String getDetail()
	{
		return this.detail;
	}

	public int getChannelId()
	{
		return channelId;
	}

	public int getColor()
	{
		return color;
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
