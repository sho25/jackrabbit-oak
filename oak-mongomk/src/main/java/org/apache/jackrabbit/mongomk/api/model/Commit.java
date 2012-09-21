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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * A higher level object representing a commit.  *  * @author<a href="mailto:pmarx@adobe.com>Philipp Marx</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Commit
block|{
comment|/**      * Returns the<a href="http://wiki.apache.org/jackrabbit/Jsop">JSOP</a> diff of this commit.      *      * @return The {@link String} representing the diff.      */
name|String
name|getDiff
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link List} of {@link Instruction}s which were created from the diff.      *      * @see #getDiff()      *      * @return The {@link List} of {@link Instruction}s.      */
name|List
argument_list|<
name|Instruction
argument_list|>
name|getInstructions
parameter_list|()
function_decl|;
comment|/**      * Returns the message of the commit.      *      * @return The message.      */
name|String
name|getMessage
parameter_list|()
function_decl|;
comment|/**      * Returns the path of the root node of this commit.      *      * @return The path of the root node.      */
name|String
name|getPath
parameter_list|()
function_decl|;
comment|/**      * Returns the revision id of this commit if known already, else this will return {@code null}. The revision      * id will be determined only after the commit has been successfully performed.      *      * @see #setRevisionId(String)      *      * @return The revision id of this commit or {@code null}.      */
name|String
name|getRevisionId
parameter_list|()
function_decl|;
comment|/**      * Sets the revision id of this commit.      *      * @see #getRevisionId()      *      * @param revisionId The revision id to set.      */
name|void
name|setRevisionId
parameter_list|(
name|String
name|revisionId
parameter_list|)
function_decl|;
comment|/**      * Returns the timestamp of this commit.      *      * @return The timestamp of this commit.      */
name|long
name|getTimestamp
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

