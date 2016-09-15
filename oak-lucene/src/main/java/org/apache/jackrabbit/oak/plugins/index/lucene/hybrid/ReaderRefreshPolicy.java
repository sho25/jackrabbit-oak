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
name|index
operator|.
name|lucene
operator|.
name|hybrid
package|;
end_package

begin_interface
specifier|public
interface|interface
name|ReaderRefreshPolicy
block|{
name|ReaderRefreshPolicy
name|NEVER
init|=
operator|new
name|ReaderRefreshPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|shouldRefresh
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|updated
parameter_list|()
block|{          }
block|}
decl_stmt|;
comment|/**      * Returns  true if refresh is to be done and      * resets the internal state. The caller which      * gets the true answer would be responsible for      * refreshing the readers.      *      *<p>For e.g. once updated the first call to      * this method would return true and subsequent      * calls return false      *      * @return true if refresh is to be done      */
name|boolean
name|shouldRefresh
parameter_list|()
function_decl|;
comment|/**      * Invoked when index gets updated      */
name|void
name|updated
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

