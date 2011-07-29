package org.geolatte.cql.hibernate;

import org.geolatte.cql.CQL;
import org.geolatte.cql.CQLLexer;
import org.geolatte.cql.lexer.LexerException;
import org.geolatte.cql.node.Start;
import org.geolatte.cql.parser.Parser;
import org.geolatte.cql.parser.ParserException;
import org.hibernate.criterion.DetachedCriteria;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.text.ParseException;

/**
 * @author Karel Maesen, Geovise BVBA
 *         creation-date: 7/29/11
 */
public class HibernateCQLAdapter extends CQL {

       /**
     * Constructs a Hibernate <tt>DetachedCriteria</tt> based on the given CQL expression, for the given class.
     * Use the <tt>DetachedCriteria.getExecutableCriteria(mySession)</tt> to get an executable <tt>Criteria<tt>.
     * @param cqlExpression The CQL expression
     * @param forClass The class of the objects on which the CQL expression will be applied.
     * @return A DetachedCriteria that corresponds to the given CQL expression.
     * @throws java.text.ParseException When parsing fails for any reason (parser, lexer, IO)
     */
    public static DetachedCriteria toCriteria(String cqlExpression, Class forClass) throws ParseException {

        try {
            Parser p = new Parser( new CQLLexer( new PushbackReader(new StringReader(cqlExpression), 1024)));
            // Parse the input.
            Start tree = p.parse();

            // Build the filter expression
            HibernateCriteriaBuilder builder = new HibernateCriteriaBuilder(forClass);
            tree.apply(builder);

            return builder.getCriteria();
        }
        catch(ParserException e) {

            ParseException parseException = new ParseException(e.getMessage(), e.getToken().getPos());
            parseException.initCause(e);
            throw parseException;
        }
        catch (LexerException e) {

            ParseException parseException = new ParseException(e.getMessage(), 0);
            parseException.initCause(e);
            throw parseException;
        }
        catch (IOException e) {

            ParseException parseException = new ParseException(e.getMessage(), 0);
            parseException.initCause(e);
            throw parseException;
        }
    }
}
