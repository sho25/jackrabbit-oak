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
name|instruction
package|;
end_package

begin_import
import|import
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
name|instruction
operator|.
name|Instruction
operator|.
name|AddNodeInstruction
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
name|mongomk
operator|.
name|api
operator|.
name|instruction
operator|.
name|Instruction
operator|.
name|CopyNodeInstruction
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
name|mongomk
operator|.
name|api
operator|.
name|instruction
operator|.
name|Instruction
operator|.
name|MoveNodeInstruction
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
name|mongomk
operator|.
name|api
operator|.
name|instruction
operator|.
name|Instruction
operator|.
name|RemoveNodeInstruction
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
name|mongomk
operator|.
name|api
operator|.
name|instruction
operator|.
name|Instruction
operator|.
name|SetPropertyInstruction
import|;
end_import

begin_comment
comment|/**  * A<a href="http://en.wikipedia.org/wiki/Visitor_pattern">Visitor</a> to iterate  * through a list of {@code Instruction}s without the need to use {@code instanceof}  * on each item.  */
end_comment

begin_interface
specifier|public
interface|interface
name|InstructionVisitor
block|{
comment|/**      * Visits a {@code AddNodeInstruction}.      *      * @param instruction The instruction.      */
name|void
name|visit
parameter_list|(
name|AddNodeInstruction
name|instruction
parameter_list|)
function_decl|;
comment|/**      * Visits a {@code CopyNodeInstruction}.      *      * @param instruction The instruction.      */
name|void
name|visit
parameter_list|(
name|CopyNodeInstruction
name|instruction
parameter_list|)
function_decl|;
comment|/**      * Visits a {@code MoveNodeInstruction}.      *      * @param instruction The instruction.      */
name|void
name|visit
parameter_list|(
name|MoveNodeInstruction
name|instruction
parameter_list|)
function_decl|;
comment|/**      * Visits a {@code RemoveNodeInstruction}.      *      * @param instruction The instruction.      */
name|void
name|visit
parameter_list|(
name|RemoveNodeInstruction
name|instruction
parameter_list|)
function_decl|;
comment|/**      * Visits a {@code SetPropertyInstruction}.      *      * @param instruction The instruction.      */
name|void
name|visit
parameter_list|(
name|SetPropertyInstruction
name|instruction
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

