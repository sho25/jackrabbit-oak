begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|ast
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|STRING
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
operator|.
name|STRINGS
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|ParseException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Tree
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|fulltext
operator|.
name|FullTextContains
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|fulltext
operator|.
name|FullTextParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|query
operator|.
name|index
operator|.
name|FilterImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|memory
operator|.
name|PropertyValues
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|query
operator|.
name|QueryIndex
operator|.
name|FulltextQueryIndex
import|;
end_import

begin_comment
comment|/**  * A fulltext "contains(...)" condition.  */
end_comment

begin_class
specifier|public
class|class
name|FullTextSearchImpl
extends|extends
name|ConstraintImpl
block|{
specifier|final
name|String
name|selectorName
decl_stmt|;
specifier|private
specifier|final
name|String
name|relativePath
decl_stmt|;
specifier|final
name|String
name|propertyName
decl_stmt|;
specifier|final
name|StaticOperandImpl
name|fullTextSearchExpression
decl_stmt|;
specifier|private
name|SelectorImpl
name|selector
decl_stmt|;
specifier|public
name|FullTextSearchImpl
parameter_list|(
name|String
name|selectorName
parameter_list|,
name|String
name|propertyName
parameter_list|,
name|StaticOperandImpl
name|fullTextSearchExpression
parameter_list|)
block|{
name|this
operator|.
name|selectorName
operator|=
name|selectorName
expr_stmt|;
name|int
name|slash
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|slash
operator|=
name|propertyName
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|slash
operator|==
operator|-
literal|1
condition|)
block|{
name|this
operator|.
name|relativePath
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|relativePath
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|slash
argument_list|)
expr_stmt|;
name|propertyName
operator|=
name|propertyName
operator|.
name|substring
argument_list|(
name|slash
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|propertyName
operator|==
literal|null
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
condition|)
block|{
name|this
operator|.
name|propertyName
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|propertyName
operator|=
name|propertyName
expr_stmt|;
block|}
name|this
operator|.
name|fullTextSearchExpression
operator|=
name|fullTextSearchExpression
expr_stmt|;
block|}
specifier|public
name|StaticOperandImpl
name|getFullTextSearchExpression
parameter_list|()
block|{
return|return
name|fullTextSearchExpression
return|;
block|}
annotation|@
name|Override
name|ConstraintImpl
name|not
parameter_list|()
block|{
return|return
operator|new
name|NotFullTextSearchImpl
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
name|boolean
name|accept
parameter_list|(
name|AstVisitor
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|visit
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|"contains("
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|selectorName
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
name|String
name|pn
init|=
name|propertyName
decl_stmt|;
if|if
condition|(
name|pn
operator|==
literal|null
condition|)
block|{
name|pn
operator|=
literal|"*"
expr_stmt|;
block|}
if|if
condition|(
name|relativePath
operator|!=
literal|null
condition|)
block|{
name|pn
operator|=
name|relativePath
operator|+
literal|"/"
operator|+
name|pn
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
name|quote
argument_list|(
name|pn
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
name|getFullTextSearchExpression
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|getPropertyExistenceConditions
parameter_list|()
block|{
if|if
condition|(
name|propertyName
operator|==
literal|null
condition|)
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
name|String
name|fullName
decl_stmt|;
if|if
condition|(
name|relativePath
operator|!=
literal|null
condition|)
block|{
name|fullName
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|relativePath
argument_list|,
name|propertyName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fullName
operator|=
name|propertyName
expr_stmt|;
block|}
return|return
name|Collections
operator|.
name|singleton
argument_list|(
operator|new
name|PropertyExistenceImpl
argument_list|(
name|selector
argument_list|,
name|selectorName
argument_list|,
name|fullName
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|FullTextExpression
name|getFullTextConstraint
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
if|if
condition|(
operator|!
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|PropertyValue
name|v
init|=
name|fullTextSearchExpression
operator|.
name|currentValue
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|p
init|=
name|propertyName
decl_stmt|;
if|if
condition|(
name|relativePath
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
name|p
operator|=
literal|"*"
expr_stmt|;
block|}
name|p
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|relativePath
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
name|String
name|p2
init|=
name|normalizePropertyName
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|String
name|rawText
init|=
name|getRawText
argument_list|(
name|v
argument_list|)
decl_stmt|;
name|FullTextExpression
name|e
init|=
name|FullTextParser
operator|.
name|parse
argument_list|(
name|p2
argument_list|,
name|rawText
argument_list|)
decl_stmt|;
return|return
operator|new
name|FullTextContains
argument_list|(
name|p2
argument_list|,
name|rawText
argument_list|,
name|e
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid expression: "
operator|+
name|fullTextSearchExpression
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
name|String
name|getRawText
parameter_list|(
name|PropertyValue
name|v
parameter_list|)
block|{
return|return
name|v
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|selector
argument_list|)
return|;
block|}
comment|/**      * verify that a property exists in the node. {@code property IS NOT NULL}      *       * @param propertyName the property to check      * @param selector the selector to work with      * @return true if the property is there, false otherwise.      */
name|boolean
name|enforcePropertyExistence
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|SelectorImpl
name|selector
parameter_list|)
block|{
name|PropertyValue
name|p
init|=
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluate
parameter_list|()
block|{
comment|// disable evaluation if a fulltext index is used,
comment|// to avoid running out of memory if the node is large,
comment|// and because we might not implement all features
comment|// such as index aggregation
if|if
condition|(
name|selector
operator|.
name|getIndex
argument_list|()
operator|instanceof
name|FulltextQueryIndex
condition|)
block|{
comment|// first verify if a property level condition exists and if that
comment|// condition checks out, this takes out some extra rows from the index
comment|// aggregation bits
if|if
condition|(
name|relativePath
operator|==
literal|null
operator|&&
name|propertyName
operator|!=
literal|null
condition|)
block|{
return|return
name|enforcePropertyExistence
argument_list|(
name|propertyName
argument_list|,
name|selector
argument_list|)
return|;
block|}
return|return
literal|true
return|;
block|}
comment|// OAK-2050
if|if
condition|(
operator|!
name|query
operator|.
name|getSettings
argument_list|()
operator|.
name|getFullTextComparisonWithoutIndex
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|relativePath
operator|==
literal|null
operator|&&
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|PropertyValue
name|p
init|=
name|selector
operator|.
name|currentProperty
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|appendString
argument_list|(
name|buff
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|path
init|=
name|selector
operator|.
name|currentPath
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|appendString
argument_list|(
name|buff
argument_list|,
name|PropertyValues
operator|.
name|newString
argument_list|(
name|PathUtils
operator|.
name|getName
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|relativePath
operator|!=
literal|null
condition|)
block|{
name|String
name|rp
init|=
name|normalizePath
argument_list|(
name|relativePath
argument_list|)
decl_stmt|;
name|path
operator|=
name|PathUtils
operator|.
name|concat
argument_list|(
name|path
argument_list|,
name|rp
argument_list|)
expr_stmt|;
block|}
name|Tree
name|tree
init|=
name|selector
operator|.
name|getTree
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|tree
operator|==
literal|null
operator|||
operator|!
name|tree
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|propertyName
operator|!=
literal|null
condition|)
block|{
name|String
name|pn
init|=
name|normalizePropertyName
argument_list|(
name|propertyName
argument_list|)
decl_stmt|;
name|PropertyState
name|p
init|=
name|tree
operator|.
name|getProperty
argument_list|(
name|pn
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|appendString
argument_list|(
name|buff
argument_list|,
name|PropertyValues
operator|.
name|create
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|PropertyState
name|p
range|:
name|tree
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|appendString
argument_list|(
name|buff
argument_list|,
name|PropertyValues
operator|.
name|create
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|getFullTextConstraint
argument_list|(
name|selector
argument_list|)
operator|.
name|evaluate
argument_list|(
name|buff
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|evaluateStop
parameter_list|()
block|{
comment|// if a fulltext index is used, then we are fine
if|if
condition|(
name|selector
operator|.
name|getIndex
argument_list|()
operator|instanceof
name|FulltextQueryIndex
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// OAK-2050
if|if
condition|(
operator|!
name|query
operator|.
name|getSettings
argument_list|()
operator|.
name|getFullTextComparisonWithoutIndex
argument_list|()
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|private
specifier|static
name|void
name|appendString
parameter_list|(
name|StringBuilder
name|buff
parameter_list|,
name|PropertyValue
name|p
parameter_list|)
block|{
if|if
condition|(
name|p
operator|.
name|isArray
argument_list|()
condition|)
block|{
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARIES
condition|)
block|{
comment|// OAK-2050: don't try to load binaries as this would
comment|// run out of memory
block|}
else|else
block|{
for|for
control|(
name|String
name|v
range|:
name|p
operator|.
name|getValue
argument_list|(
name|STRINGS
argument_list|)
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
name|v
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
if|if
condition|(
name|p
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BINARY
condition|)
block|{
comment|// OAK-2050: don't try to load binaries as this would
comment|// run out of memory
block|}
else|else
block|{
name|buff
operator|.
name|append
argument_list|(
name|p
operator|.
name|getValue
argument_list|(
name|STRING
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|bindSelector
parameter_list|(
name|SourceImpl
name|source
parameter_list|)
block|{
name|selector
operator|=
name|source
operator|.
name|getExistingSelector
argument_list|(
name|selectorName
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
block|{
name|f
operator|.
name|restrictFulltextCondition
argument_list|(
name|fullTextSearchExpression
operator|.
name|currentValue
argument_list|()
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
if|if
condition|(
name|s
operator|.
name|equals
argument_list|(
name|selector
argument_list|)
condition|)
block|{
name|selector
operator|.
name|restrictSelector
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * restrict the provided property to the property to the provided filter achieving so      * {@code property IS NOT NULL}      *       * @param propertyName      * @param f      */
name|void
name|restrictPropertyOnFilter
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|FilterImpl
name|f
parameter_list|)
block|{
name|f
operator|.
name|restrictProperty
argument_list|(
name|propertyName
argument_list|,
name|Operator
operator|.
name|NOT_EQUAL
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|AstElement
name|copyOf
parameter_list|()
block|{
return|return
operator|new
name|FullTextSearchImpl
argument_list|(
name|selectorName
argument_list|,
name|propertyName
argument_list|,
name|fullTextSearchExpression
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|requiresFullTextIndex
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

