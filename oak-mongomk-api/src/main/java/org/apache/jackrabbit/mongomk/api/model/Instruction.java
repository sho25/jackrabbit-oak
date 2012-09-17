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
name|mongomk
operator|.
name|api
operator|.
name|model
package|;
end_package

begin_comment
comment|/**  * An {@code Instruction} is an abstraction of a single<a href="http://wiki.apache.org/jackrabbit/Jsop">JSOP</a>  * operation.  *  *<p>  * Each operation is a concrete subinterface of {@code Instruction} and extending it by the specific properties of the  * operation. There is no exact 1 : 1 mapping between a {@code JSOP} operation and a subinterface, i.e. in {@code JSOP}  * there is one add operation for adding nodes and properties whereas there are two specific subinterfaces; one for  * adding a node and one for adding a property.  *</p>  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Instruction
block|{
comment|/**      * Accepts an {@code InstructionVisitor}.      *      * @param visitor The visitor.      */
name|void
name|accept
parameter_list|(
name|InstructionVisitor
name|visitor
parameter_list|)
function_decl|;
comment|/**      * Returns the path of this {@code Instruction}.      *      *<p>      * The semantics of this property differ depending on the concrete subinterface.      *</p>      *      * @return The path.      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * The add node operation => "+" STRING ":" (OBJECT).      */
specifier|public
interface|interface
name|AddNodeInstruction
extends|extends
name|Instruction
block|{     }
comment|/**      * The add property operation => "+" STRING ":" (ATOM | ARRAY)      */
specifier|public
interface|interface
name|AddPropertyInstruction
extends|extends
name|Instruction
block|{
comment|/**          * Returns the key of the property to add.          *          * @return The key.          */
name|String
name|getKey
parameter_list|()
function_decl|;
comment|/**          * Returns the value of the property to add.          *          * @return The value.          */
name|Object
name|getValue
parameter_list|()
function_decl|;
block|}
comment|/**      * The copy node operation => "*" STRING ":" STRING      */
specifier|public
interface|interface
name|CopyNodeInstruction
extends|extends
name|Instruction
block|{
comment|/**          * Returns the destination path.          *          * @return the destination path.          */
name|String
name|getDestPath
parameter_list|()
function_decl|;
comment|/**          * Returns the source path.          *          * @return the source path.          */
name|String
name|getSourcePath
parameter_list|()
function_decl|;
block|}
comment|/**      * The move node operation => ">" STRING ":" STRING      */
specifier|public
interface|interface
name|MoveNodeInstruction
extends|extends
name|Instruction
block|{
comment|/**          * Returns the destination path.          *          * @return the destination path.          */
name|String
name|getDestPath
parameter_list|()
function_decl|;
comment|/**          * Returns the source path.          *          * @return the source path.          */
name|String
name|getSourcePath
parameter_list|()
function_decl|;
block|}
comment|/**      * The remove node operation => "-" STRING      */
specifier|public
interface|interface
name|RemoveNodeInstruction
extends|extends
name|Instruction
block|{     }
comment|/**      * The set property operation => "^" STRING ":" ATOM | ARRAY      */
specifier|public
interface|interface
name|SetPropertyInstruction
extends|extends
name|Instruction
block|{
comment|/**          * Returns the key of the property to set.          *          * @return The key.          */
name|String
name|getKey
parameter_list|()
function_decl|;
comment|/**          * Returns the value of the property to set.          *          * @return The value.          */
name|Object
name|getValue
parameter_list|()
function_decl|;
block|}
block|}
end_interface

end_unit

