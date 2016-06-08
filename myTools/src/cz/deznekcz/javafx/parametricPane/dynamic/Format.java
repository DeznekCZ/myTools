package cz.deznekcz.javafx.parametricPane.dynamic;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.deznekcz.javafx.parametricPane.ParametricPane;
import cz.deznekcz.reference.Out.OutString;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Format extends StringBinding {

	private static final List<Format> formates = new ArrayList<>();
	private final ArrayList<StringProperty> formatMaches;
	private final String name;
	
	private Format(String name, ArrayList<StringProperty> formatMaches) {
		this.name = name;
		this.formatMaches = formatMaches;
	}

	public static void create(String format, StringProperty property) {
		int startIndex = 0, endIndex;
		
		StringBuilder nameBuilder = new StringBuilder();
		
		ArrayList<StringProperty> formatMaches = new ArrayList<StringProperty>();
		Matcher formatMatcher = Pattern.compile("\\$(\\[[a-zA-Z]+\\])*\\{[a-zA-Z]+\\}")
				.matcher(format);
		while (formatMatcher.find()) {
			endIndex = formatMatcher.start();
			if (endIndex != startIndex) {
				formatMaches.add(Format.String(format.substring(startIndex, endIndex)));
			}
			String variable = formatMatcher.group();
			formatMaches.add(Format.Variable(variable));
			nameBuilder.append(", ").append(variable.substring(variable.lastIndexOf('{')+1, variable.length()-1));
			
			startIndex = formatMatcher.end();
		}
		if (startIndex < format.length()) {
			formatMaches.add(Format.String(format.substring(startIndex)));
		}
		
		Format f = new Format("Formater: " + nameBuilder.substring(2), formatMaches);
		formates.add(f);
		f.bind(formatMaches.toArray(new Property[formatMaches.size()]));
		f.addListener((e,l,n) -> property.setValue(n));
	}

	private static StringProperty Variable(String variableData) {
		StringProperty variable = new SimpleStringProperty();
		String elementName = variableData.substring(variableData.indexOf('{')+1, variableData.length()-1);
		StringProperty bind = ParametricPane.getInstance().parameterValueByName(elementName);
		variable.bind(bind);
		
		List<Modifiers> modifiers = new ArrayList<>(1);
		
		Matcher formatMatcher = Pattern.compile("\\[[a-zA-Z]+\\]")
				.matcher(variableData);
		while (formatMatcher.find()) {
			modifiers.add(
					Modifiers.valueOf(
							variableData.substring(
									formatMatcher.start()+1, 
									formatMatcher.end()-1
							)
					)
			);
		}
		
		if (modifiers.size() > 0) {
			variable.bind(new StringBinding() {
				final List<Modifiers> mods;
				{
					this.mods = modifiers;
					bind(bind);
				}
				@Override
				protected String computeValue() {
					String modified = bind.get();
					modified = (modified != null ? modified : "");
					for (Modifiers mod : mods) {
						modified = mod.apply(modified);
					}
					return modified;
				}
			});
		}
		
		return variable;
	}

	private static StringProperty String(String string) {
		return new ReadOnlyStringWrapper(Pattern.compile("\\$\\$").matcher(string).replaceAll("\\$"));
	}

	@Override
	protected String computeValue() {
		System.out.println(name);
		OutString string = OutString.empty();
		for (StringProperty value : formatMaches) {
			string.append(value.getValue());
		}
		return string.get();
	}
}
