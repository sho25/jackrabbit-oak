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
name|query
operator|.
name|index
operator|.
name|FilterImpl
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
comment|/**      * Evaluate the result using the currently set values.      *      * @return true if the constraint matches      */
specifier|public
specifier|abstract
name|boolean
name|evaluate
parameter_list|()
function_decl|;
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
comment|/**      * Push as much of the condition down to this selector, further restricting      * the selector condition if possible.      *      * @param s the selector      */
specifier|public
specifier|abstract
name|void
name|restrictPushDown
parameter_list|(
name|SelectorImpl
name|s
parameter_list|)
function_decl|;
block|}
end_class

end_unit

