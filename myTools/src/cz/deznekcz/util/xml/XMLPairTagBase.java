package cz.deznekcz.util.xml;

public abstract class XMLPairTagBase<ROOT> extends XMLelem<ROOT, XMLPairTagBase<ROOT>> {
	
	public XMLPairTagBase(String name, ROOT parent, boolean expanded) {
		super(name, parent, expanded);
	}

	public abstract <THIS extends XMLPairTagBase<ROOT>> XMLSingleTag<THIS> newSingleTag(String name);
	public abstract <THIS extends XMLPairTagBase<ROOT>> XMLPairTag<THIS> newPairTag(String name);
	public abstract <THIS extends XMLPairTagBase<ROOT>> XMLPairTag<THIS> newPairTag(String name, boolean expanded);
	public abstract <THIS extends XMLPairTagBase<ROOT>> THIS setText(String text);
	public abstract <THIS extends XMLPairTagBase<ROOT>> THIS setTextCDATA(String text);
}
