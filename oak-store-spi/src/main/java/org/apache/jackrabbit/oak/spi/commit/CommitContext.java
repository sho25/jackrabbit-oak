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
name|spi
operator|.
name|commit
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

begin_comment
comment|/**  * A CommitContext instance can be obtained from {@link CommitInfo#getInfo()}  * if it has been set before the merge call. This can then be used by CommitHook  * to record some metadata regarding the commit.  *  *<p>CommitContext state would be reset in case commit is retried from within  * NodeStore say when a merge exception occurs.  */
end_comment

begin_interface
specifier|public
interface|interface
name|CommitContext
block|{
comment|/**      * Name of the entry of the mutable commit attributes map in the {@code info}      * map in {@link CommitInfo#getInfo()}      */
name|String
name|NAME
init|=
literal|"oak.commitAttributes"
decl_stmt|;
comment|/**      * Stores an attribute related to this commit.      * Attributes are reset if the commit is retried.      *      *<p>If the object passed in is null, the effect is the same as      * calling {@link #remove}.      *      * @param name a<code>String</code> specifying the name of the attribute      * @param value the<code>Object</code> to be stored      */
name|void
name|set
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the value of the named attribute as an<code>Object</code>,      * or<code>null</code> if no attribute of the given name exists.      *      * @param name<code>String</code> specifying the name of      * the attribute      *      * @return an<code>Object</code> containing the value      * of the attribute, or<code>null</code> if the attribute does not exist      */
annotation|@
name|Nullable
name|Object
name|get
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
comment|/**      * Removes an attribute from this commit.      *      * @param name a<code>String</code> specifying      * the name of the attribute to remove      */
name|void
name|remove
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

