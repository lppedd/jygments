/**
 * Copyright 2010 Three Crickets LLC.
 * <p>
 * The contents of this file are subject to the terms of a BSD license. See
 * attached license.txt.
 * <p>
 * Alternatively, you can obtain a royalty free commercial license with less
 * limitations, transferable or non-transferable, directly from Three Crickets
 * at http://threecrickets.com/
 */

package com.threecrickets.jygments.grammar.def;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.threecrickets.jygments.Def;
import com.threecrickets.jygments.ResolutionException;
import com.threecrickets.jygments.grammar.Grammar;
import com.threecrickets.jygments.grammar.State;
import com.threecrickets.jygments.grammar.TokenRule;
import com.threecrickets.jygments.grammar.TokenType;

/**
 * @author Tal Liron
 */
public class TokenRuleDef extends Def<Grammar>
{
	public TokenRuleDef( String stateName, String pattern, List<String> tokenTypeNames )
	{
		this.stateName = stateName;
		this.pattern = pattern;
		this.tokenTypeNames = tokenTypeNames;
	}

	public TokenRuleDef( String stateName, String pattern, String... tokenTypeNames )
	{
		this.stateName = stateName;
		this.pattern = pattern;
		ArrayList<String> list = new ArrayList<String>( tokenTypeNames.length );
		for( String tokenTypeName : tokenTypeNames )
			list.add( tokenTypeName );
		this.tokenTypeNames = list;
	}

	//
	// Attributes
	//

	public String getStateName()
	{
		return stateName;
	}

	public String getPattern()
	{
		return pattern;
	}

	public List<String> getTokenTypeNames()
	{
		return tokenTypeNames;
	}

	//
	// Def
	//

	@Override
	public boolean resolve( Grammar grammar ) throws ResolutionException
	{
		Pattern pattern;
		try
		{
			pattern = Pattern.compile( this.pattern, Pattern.MULTILINE );
		}
		catch( PatternSyntaxException x )
		{
			throw new ResolutionException( "RegEx syntax error: " + this.pattern, x );
		}

		ArrayList<TokenType> tokenTypes = new ArrayList<TokenType>();
		for( String tokenTypeName : tokenTypeNames )
		{
			TokenType tokenType = TokenType.getTokenTypeByName( tokenTypeName );
			if( tokenType == null )
				throw new ResolutionException( "Unknown token type: " + tokenTypeName );
			tokenTypes.add( tokenType );
		}

		TokenRule rule = createTokenRule( pattern, tokenTypes, grammar );
		State state = grammar.getState( stateName );
		state.addRule( rule );
		resolved = true;
		return true;
	}

	//
	// Object
	//

	@Override
	public String toString()
	{
		return super.toString() + " " + stateName + ", " + pattern + ", " + tokenTypeNames;
	}

	// //////////////////////////////////////////////////////////////////////////
	// Protected

	protected TokenRule createTokenRule( Pattern pattern, List<TokenType> tokenTypes, Grammar grammar ) throws ResolutionException
	{
		return new TokenRule( pattern, tokenTypes );
	}

	// //////////////////////////////////////////////////////////////////////////
	// Private

	private final String stateName;

	private final String pattern;

	private final List<String> tokenTypeNames;
}
