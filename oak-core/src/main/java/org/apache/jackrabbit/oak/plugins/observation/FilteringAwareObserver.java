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
name|observation
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
name|oak
operator|.
name|spi
operator|.
name|commit
operator|.
name|CommitInfo
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
name|state
operator|.
name|NodeState
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
comment|/**  * A FilteringAwareObserver is the stateless-variant of  * an Observer which gets an explicit before as well as the  * after NodeState.  *<p>  * It is used by the FilteringObserver (or more precisely  * by the FilteringDispatcher) to support skipping (ie filtering)  * of content changes.  */
end_comment

begin_interface
specifier|public
interface|interface
name|FilteringAwareObserver
block|{
comment|/**      * Equivalent to the state-full contentChanged() method of the Observer      * with one important difference being that this variation explicitly      * passes the before NodeState (thus the observer must in this case      * not remember the previous state)      * @param before the before NodeState      * @param after the after NodeState      * @param info the associated CommitInfo      */
name|void
name|contentChanged
parameter_list|(
annotation|@
name|NotNull
name|NodeState
name|before
parameter_list|,
annotation|@
name|NotNull
name|NodeState
name|after
parameter_list|,
annotation|@
name|NotNull
name|CommitInfo
name|info
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

