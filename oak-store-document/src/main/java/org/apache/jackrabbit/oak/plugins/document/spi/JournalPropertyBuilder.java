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
name|plugins
operator|.
name|document
operator|.
name|spi
package|;
end_package

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_interface
specifier|public
interface|interface
name|JournalPropertyBuilder
parameter_list|<
name|T
extends|extends
name|JournalProperty
parameter_list|>
block|{
comment|/**      * Adds the JournalProperty instance fetched from CommitInfo to this builder      */
name|void
name|addProperty
parameter_list|(
annotation|@
name|Nullable
name|T
name|journalProperty
parameter_list|)
function_decl|;
comment|/**      * Returns a string representation state of the builder which      * would be stored in JournalEntry      */
name|String
name|buildAsString
parameter_list|()
function_decl|;
comment|/**      * Adds the serialized form of journal property (as build from #buildAsString)      * call      */
name|void
name|addSerializedProperty
parameter_list|(
annotation|@
name|Nullable
name|String
name|serializedProperty
parameter_list|)
function_decl|;
comment|/**      * Constructs a JournalProperty instance based on current builder state      */
name|JournalProperty
name|build
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

