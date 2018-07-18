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
name|ast
package|;
end_package

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
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_comment
comment|/**  * The base class for constraints.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ConstraintImpl
extends|extends
name|AstElement
block|{
comment|/**      * Simplify the expression if possible, for example by removing duplicate expressions.      * For example, "x=1 or x=1" should be simplified to "x=1".      *       * @return the simplified constraint, or "this" if it is not possible to simplify      */
specifier|public
name|ConstraintImpl
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/**      * Get the negative constraint, if it is simpler, or null. For example,      * "not x = 1" returns "x = 1", but "x = 1" returns null.      *       * @return the negative constraint, or null      */
name|ConstraintImpl
name|not
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Evaluate the result using the currently set values.      *      * @return true if the constraint matches      */
specifier|public
specifier|abstract
name|boolean
name|evaluate
parameter_list|()
function_decl|;
comment|/**      * Whether this condition will, from now on, always evaluate to false. This      * is the case for example for full-text constraints if there is no      * full-text index (unless FullTextComparisonWithoutIndex is enabled). This      * will also allow is to add conditions that stop further processing for      * other reasons, similar to {@code "WHERE ROWNUM< 10"} in Oracle.      *       * @return true if further processing should be stopped      */
specifier|public
name|boolean
name|evaluateStop
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Get the set of property existence conditions that can be derived for this      * condition. For example, for the condition "x=1 or x=2", the property      * existence condition is "x is not null". For the condition "x=1 or y=2",      * there is no such condition. For the condition "x=1 and y=1", there are      * two (x is not null, and y is not null).      *       * @return the common property existence condition (possibly empty)      */
specifier|public
specifier|abstract
name|Set
argument_list|<
name|PropertyExistenceImpl
argument_list|>
name|getPropertyExistenceConditions
parameter_list|()
function_decl|;
comment|/**      * Get the (combined) full-text constraint. For constraints of the form      * "contains(*, 'x') or contains(*, 'y')", the combined expression is      * returned. If there is none, null is returned. For constraints of the form      * "contains(*, 'x') or z=1", null is returned as the full-text index cannot      * be used in this case for filtering (as it might filter out the z=1      * nodes).      *       * @param s the selector      * @return the full-text constraint, if there is any, or null if not      */
specifier|public
name|FullTextExpression
name|getFullTextConstraint
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
comment|/**      * Get the set of selectors for the given condition.      *       * @return the set of selectors (possibly empty)      */
specifier|public
specifier|abstract
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
function_decl|;
comment|/**      * Apply the condition to the filter, further restricting the filter if      * possible. This may also verify the data types are compatible, and that      * paths are valid.      *      * @param f the filter      */
specifier|public
specifier|abstract
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|)
function_decl|;
comment|/**      * Push as much of the condition down to this selector, further restricting      * the selector condition if possible. This is important for a join: for      * example, the query      * "select * from a inner join b on a.x=b.x where a.y=1 and b.y=1", the      * condition "a.y=1" can be pushed down to "a", and the condition "b.y=1"      * can be pushed down to "b". That means it is possible to use an index in      * this case.      *      * @param s the selector      */
specifier|public
specifier|abstract
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|==
name|this
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|ConstraintImpl
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
name|other
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|.
name|hashCode
argument_list|()
return|;
block|}
comment|/**      * Whether the constraint contains a fulltext condition that requires      * using a fulltext index, because the condition can only be evaluated there.      *       * @return true if yes      */
specifier|public
name|boolean
name|requiresFullTextIndex
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Whether the condition contains a fulltext condition that can not be       * applied to the filter, for example because it is part of an "or" condition      * of the form "where a=1 or contains(., 'x')".      *       * @return true if yes      */
specifier|public
name|boolean
name|containsUnfilteredFullTextCondition
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
comment|/**      * Compute a set of sub-constraints that could be used for composing UNION      * statements. For example in case of "c=1 or c=2", it will return to the      * caller {@code [c=1, c=2]}. Those can be later on used for re-composing      * conditions.      *<p>      * If it is not possible to convert to a union, it must return an empty set.      *<p>      * The default implementation in {@link ConstraintImpl#convertToUnion()}      * always return an empty set.      *       * @return the set of union constraints, if available, or an empty set if      *         conversion is not possible      */
annotation|@
name|NotNull
specifier|public
name|Set
argument_list|<
name|ConstraintImpl
argument_list|>
name|convertToUnion
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

