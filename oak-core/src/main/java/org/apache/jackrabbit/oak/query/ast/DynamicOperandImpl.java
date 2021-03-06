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
name|List
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
name|spi
operator|.
name|query
operator|.
name|QueryConstants
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
name|OrderEntry
import|;
end_import

begin_comment
comment|/**  * The base class for dynamic operands (such as a function or property).  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|DynamicOperandImpl
extends|extends
name|AstElement
block|{
specifier|public
specifier|abstract
name|PropertyValue
name|currentProperty
parameter_list|()
function_decl|;
comment|/**      * Apply a restriction of type "this = value" to the given filter.      *       * @param f the filter where the restriction is applied.      * @param operator the operator (for example "=").      * @param v the value      */
specifier|public
specifier|abstract
name|void
name|restrict
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|Operator
name|operator
parameter_list|,
name|PropertyValue
name|v
parameter_list|)
function_decl|;
comment|/**      * Apply a restriction of type "this in (list)" to the given filter.      *       * @param f the filter where the restriction is applied.      * @param list the list of values      */
specifier|public
specifier|abstract
name|void
name|restrictList
parameter_list|(
name|FilterImpl
name|f
parameter_list|,
name|List
argument_list|<
name|PropertyValue
argument_list|>
name|list
parameter_list|)
function_decl|;
comment|/**      * Get the function of a function-based index, in Polish notation.      *       * @param s the selector      * @return the function, or null if not supported      */
specifier|public
specifier|abstract
name|String
name|getFunction
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
function_decl|;
comment|/**      * Check whether the condition can be applied to a selector (to restrict the      * selector). The method may return true if the operand can be evaluated      * when the given selector and all previous selectors in the join can be      * evaluated.      *      * @param s the selector      * @return true if the condition can be applied      */
specifier|public
specifier|abstract
name|boolean
name|canRestrictSelector
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|supportsRangeConditions
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
specifier|abstract
name|int
name|getPropertyType
parameter_list|()
function_decl|;
comment|/**      * Get the property existence condition for this operand, if this operand is      * used as part of a condition.      *       * @return the property existence condition, or null if none      */
specifier|public
specifier|abstract
name|PropertyExistenceImpl
name|getPropertyExistence
parameter_list|()
function_decl|;
comment|/**      * Get the set of selectors for this operand.      *       * @return the set of selectors      */
specifier|public
specifier|abstract
name|Set
argument_list|<
name|SelectorImpl
argument_list|>
name|getSelectors
parameter_list|()
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
name|this
operator|==
name|other
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
name|DynamicOperandImpl
operator|)
condition|)
block|{
return|return
literal|false
return|;
block|}
name|DynamicOperandImpl
name|o
init|=
operator|(
name|DynamicOperandImpl
operator|)
name|other
decl_stmt|;
return|return
name|o
operator|.
name|toString
argument_list|()
operator|.
name|equals
argument_list|(
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
specifier|public
specifier|abstract
name|DynamicOperandImpl
name|createCopy
parameter_list|()
function_decl|;
comment|/**      * Create an entry for the "order by" list for a given filter.      *       * @param s the selector      * @param o the ordering      * @return the entry      */
specifier|public
specifier|abstract
name|OrderEntry
name|getOrderEntry
parameter_list|(
name|SelectorImpl
name|s
parameter_list|,
name|OrderingImpl
name|o
parameter_list|)
function_decl|;
comment|/**      *      * @param s      * @return the property name as defined in the OrderEntry for the DynamicOperand      */
specifier|public
name|String
name|getOrderEntryPropertyName
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
block|{
name|String
name|fn
init|=
name|getFunction
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|fn
operator|!=
literal|null
condition|)
block|{
return|return
name|QueryConstants
operator|.
name|FUNCTION_RESTRICTION_PREFIX
operator|+
name|fn
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

