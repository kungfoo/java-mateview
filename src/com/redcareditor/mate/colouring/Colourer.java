package com.redcareditor.mate.colouring;

import java.util.List;

import com.redcareditor.mate.Scope;
import com.redcareditor.theme.Theme;

public interface Colourer {

	public abstract void setTheme(Theme theme);

	public abstract Theme getTheme();

	public abstract void uncolourScopes(List<Scope> scopes);

	public abstract void uncolourScope(Scope scope, boolean something);

	public abstract void colourLineWithScopes(List<Scope> scopes);

}