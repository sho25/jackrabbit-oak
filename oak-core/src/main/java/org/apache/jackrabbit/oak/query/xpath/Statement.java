begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|xpath
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
name|QueryImpl
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
name|xpath
operator|.
name|Expression
operator|.
name|AndCondition
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
name|xpath
operator|.
name|Expression
operator|.
name|Contains
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
name|xpath
operator|.
name|Expression
operator|.
name|OrCondition
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
name|xpath
operator|.
name|Expression
operator|.
name|Property
import|;
end_import

begin_comment
comment|/**  * An xpath statement.  */
end_comment

begin_class
specifier|public
class|class
name|Statement
block|{
specifier|private
name|String
name|xpathQuery
decl_stmt|;
specifier|private
name|boolean
name|explain
decl_stmt|;
specifier|private
name|boolean
name|measure
decl_stmt|;
comment|/**      * The selector to get the columns from (the selector used in the select      * column list).      */
specifier|private
name|Selector
name|columnSelector
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|columnList
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * All selectors.      */
specifier|private
name|ArrayList
argument_list|<
name|Selector
argument_list|>
name|selectors
decl_stmt|;
specifier|private
name|Expression
name|where
decl_stmt|;
specifier|private
name|ArrayList
argument_list|<
name|Order
argument_list|>
name|orderList
init|=
operator|new
name|ArrayList
argument_list|<
name|Order
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|Statement
name|optimize
parameter_list|()
block|{
if|if
condition|(
name|explain
operator|||
name|measure
operator|||
name|orderList
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
if|if
condition|(
name|where
operator|==
literal|null
condition|)
block|{
return|return
name|this
return|;
block|}
if|if
condition|(
name|where
operator|instanceof
name|OrCondition
condition|)
block|{
name|OrCondition
name|or
init|=
operator|(
name|OrCondition
operator|)
name|where
decl_stmt|;
if|if
condition|(
name|or
operator|.
name|getCommonLeftPart
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// @x = 1 or @x = 2
comment|// is automatically converted to
comment|// @x in (1, 2)
comment|// within the query engine
block|}
elseif|else
if|if
condition|(
name|or
operator|.
name|left
operator|instanceof
name|Contains
operator|&&
name|or
operator|.
name|right
operator|instanceof
name|Contains
condition|)
block|{
comment|// do not optimize "contains"
block|}
else|else
block|{
comment|// conditions of type
comment|// @x = 1 or @y = 2
comment|// or similar are converted to
comment|// (@x = 1) union (@y = 2)
name|Statement
name|s1
init|=
operator|new
name|Statement
argument_list|()
decl_stmt|;
name|s1
operator|.
name|columnSelector
operator|=
name|columnSelector
expr_stmt|;
name|s1
operator|.
name|selectors
operator|=
name|selectors
expr_stmt|;
name|s1
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|s1
operator|.
name|where
operator|=
name|or
operator|.
name|left
expr_stmt|;
name|Statement
name|s2
init|=
operator|new
name|Statement
argument_list|()
decl_stmt|;
name|s2
operator|.
name|columnSelector
operator|=
name|columnSelector
expr_stmt|;
name|s2
operator|.
name|selectors
operator|=
name|selectors
expr_stmt|;
name|s2
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|s2
operator|.
name|where
operator|=
name|or
operator|.
name|right
expr_stmt|;
name|s2
operator|.
name|xpathQuery
operator|=
name|xpathQuery
expr_stmt|;
return|return
operator|new
name|UnionStatement
argument_list|(
name|s1
operator|.
name|optimize
argument_list|()
argument_list|,
name|s2
operator|.
name|optimize
argument_list|()
argument_list|)
return|;
block|}
block|}
elseif|else
if|if
condition|(
name|where
operator|instanceof
name|AndCondition
condition|)
block|{
comment|// conditions of type
comment|// @a = 1 and (@x = 1 or @y = 2)
comment|// are automatically converted to
comment|// (@a = 1 and @x = 1) union (@a = 1 and @y = 2)
name|AndCondition
name|and
init|=
operator|(
name|AndCondition
operator|)
name|where
decl_stmt|;
if|if
condition|(
name|and
operator|.
name|left
operator|instanceof
name|OrCondition
operator|&&
operator|!
operator|(
name|and
operator|.
name|right
operator|instanceof
name|OrCondition
operator|)
condition|)
block|{
comment|// swap left and right
name|and
operator|=
operator|new
name|AndCondition
argument_list|(
name|and
operator|.
name|right
argument_list|,
name|and
operator|.
name|left
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|and
operator|.
name|right
operator|instanceof
name|OrCondition
condition|)
block|{
name|OrCondition
name|or
init|=
operator|(
name|OrCondition
operator|)
name|and
operator|.
name|right
decl_stmt|;
if|if
condition|(
name|or
operator|.
name|getCommonLeftPart
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// @x = 1 or @x = 2
comment|// is automatically converted to
comment|// @x in (1, 2)
comment|// within the query engine
block|}
elseif|else
if|if
condition|(
name|or
operator|.
name|left
operator|instanceof
name|Contains
operator|&&
name|or
operator|.
name|right
operator|instanceof
name|Contains
condition|)
block|{
comment|// do not optimize "contains"
block|}
else|else
block|{
comment|// same as above, but with the added "and"
comment|// TODO avoid code duplication if possible
name|Statement
name|s1
init|=
operator|new
name|Statement
argument_list|()
decl_stmt|;
name|s1
operator|.
name|columnSelector
operator|=
name|columnSelector
expr_stmt|;
name|s1
operator|.
name|selectors
operator|=
name|selectors
expr_stmt|;
name|s1
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|s1
operator|.
name|where
operator|=
operator|new
name|AndCondition
argument_list|(
name|and
operator|.
name|left
argument_list|,
name|or
operator|.
name|left
argument_list|)
expr_stmt|;
name|Statement
name|s2
init|=
operator|new
name|Statement
argument_list|()
decl_stmt|;
name|s2
operator|.
name|columnSelector
operator|=
name|columnSelector
expr_stmt|;
name|s2
operator|.
name|selectors
operator|=
name|selectors
expr_stmt|;
name|s2
operator|.
name|columnList
operator|=
name|columnList
expr_stmt|;
name|s2
operator|.
name|where
operator|=
operator|new
name|AndCondition
argument_list|(
name|and
operator|.
name|left
argument_list|,
name|or
operator|.
name|right
argument_list|)
expr_stmt|;
name|s2
operator|.
name|xpathQuery
operator|=
name|xpathQuery
expr_stmt|;
return|return
operator|new
name|UnionStatement
argument_list|(
name|s1
operator|.
name|optimize
argument_list|()
argument_list|,
name|s2
operator|.
name|optimize
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
return|return
name|this
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
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// explain | measure ...
if|if
condition|(
name|explain
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"explain "
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|measure
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|"measure "
argument_list|)
expr_stmt|;
block|}
comment|// select ...
name|buff
operator|.
name|append
argument_list|(
literal|"select "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
operator|new
name|Expression
operator|.
name|Property
argument_list|(
name|columnSelector
argument_list|,
name|QueryImpl
operator|.
name|JCR_PATH
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectors
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|QueryImpl
operator|.
name|JCR_PATH
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
operator|new
name|Expression
operator|.
name|Property
argument_list|(
name|columnSelector
argument_list|,
name|QueryImpl
operator|.
name|JCR_SCORE
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectors
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
operator|.
name|append
argument_list|(
name|QueryImpl
operator|.
name|JCR_SCORE
argument_list|)
operator|.
name|append
argument_list|(
literal|']'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|columnList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
operator|new
name|Expression
operator|.
name|Property
argument_list|(
name|columnSelector
argument_list|,
literal|"*"
argument_list|,
literal|false
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|columnList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|Expression
name|e
init|=
name|columnList
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|columnName
init|=
name|e
operator|.
name|toString
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|columnName
argument_list|)
expr_stmt|;
if|if
condition|(
name|selectors
operator|.
name|size
argument_list|()
operator|>
literal|1
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" as ["
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|getColumnAliasName
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|// from ...
name|buff
operator|.
name|append
argument_list|(
literal|" from "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|selectors
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Selector
name|s
init|=
name|selectors
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" inner join "
argument_list|)
expr_stmt|;
block|}
name|String
name|nodeType
init|=
name|s
operator|.
name|nodeType
decl_stmt|;
if|if
condition|(
name|nodeType
operator|==
literal|null
condition|)
block|{
name|nodeType
operator|=
literal|"nt:base"
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|'['
operator|+
name|nodeType
operator|+
literal|']'
argument_list|)
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
name|s
operator|.
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|s
operator|.
name|joinCondition
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" on "
argument_list|)
operator|.
name|append
argument_list|(
name|s
operator|.
name|joinCondition
argument_list|)
expr_stmt|;
block|}
block|}
comment|// where ...
if|if
condition|(
name|where
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" where "
argument_list|)
operator|.
name|append
argument_list|(
name|where
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// order by ...
if|if
condition|(
operator|!
name|orderList
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderList
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
name|orderList
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|// leave original xpath string as a comment
if|if
condition|(
name|xpathQuery
operator|!=
literal|null
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|" /* xpath: "
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|xpathQuery
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|void
name|setExplain
parameter_list|(
name|boolean
name|explain
parameter_list|)
block|{
name|this
operator|.
name|explain
operator|=
name|explain
expr_stmt|;
block|}
specifier|public
name|void
name|setMeasure
parameter_list|(
name|boolean
name|measure
parameter_list|)
block|{
name|this
operator|.
name|measure
operator|=
name|measure
expr_stmt|;
block|}
specifier|public
name|void
name|addSelectColumn
parameter_list|(
name|Property
name|p
parameter_list|)
block|{
name|columnList
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setSelectors
parameter_list|(
name|ArrayList
argument_list|<
name|Selector
argument_list|>
name|selectors
parameter_list|)
block|{
name|this
operator|.
name|selectors
operator|=
name|selectors
expr_stmt|;
block|}
specifier|public
name|void
name|setWhere
parameter_list|(
name|Expression
name|where
parameter_list|)
block|{
name|this
operator|.
name|where
operator|=
name|where
expr_stmt|;
block|}
specifier|public
name|void
name|addOrderBy
parameter_list|(
name|Order
name|order
parameter_list|)
block|{
name|this
operator|.
name|orderList
operator|.
name|add
argument_list|(
name|order
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setColumnSelector
parameter_list|(
name|Selector
name|columnSelector
parameter_list|)
block|{
name|this
operator|.
name|columnSelector
operator|=
name|columnSelector
expr_stmt|;
block|}
specifier|public
name|void
name|setOriginalQuery
parameter_list|(
name|String
name|xpathQuery
parameter_list|)
block|{
name|this
operator|.
name|xpathQuery
operator|=
name|xpathQuery
expr_stmt|;
block|}
comment|/**      * A union statement.      */
specifier|static
class|class
name|UnionStatement
extends|extends
name|Statement
block|{
specifier|private
specifier|final
name|Statement
name|s1
decl_stmt|,
name|s2
decl_stmt|;
name|UnionStatement
parameter_list|(
name|Statement
name|s1
parameter_list|,
name|Statement
name|s2
parameter_list|)
block|{
name|this
operator|.
name|s1
operator|=
name|s1
expr_stmt|;
name|this
operator|.
name|s2
operator|=
name|s2
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|s1
operator|+
literal|" union "
operator|+
name|s2
return|;
block|}
block|}
block|}
end_class

end_unit

