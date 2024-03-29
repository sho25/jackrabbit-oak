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
name|jcr
operator|.
name|security
operator|.
name|authorization
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

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
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|Event
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
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
name|test
operator|.
name|api
operator|.
name|observation
operator|.
name|EventResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * Permission evaluation tests related to observation.  */
end_comment

begin_class
specifier|public
class|class
name|ObservationTest
extends|extends
name|AbstractEvaluationTest
block|{
specifier|private
specifier|static
specifier|final
name|long
name|DEFAULT_WAIT_TIMEOUT
init|=
literal|5000
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testEventGeneration
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw the READ privilege
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
comment|// testUser registers a event listener for 'path
name|ObservationManager
name|obsMgr
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|EventResult
name|listener
init|=
operator|new
name|EventResult
argument_list|(
name|this
operator|.
name|log
argument_list|)
decl_stmt|;
try|try
block|{
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|Event
operator|.
name|NODE_REMOVED
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// superuser removes the node with childNPath& siblingPath in
comment|// order to provoke events being generated
name|superuser
operator|.
name|getItem
argument_list|(
name|childNPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|getItem
argument_list|(
name|siblingPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// since the testUser does not have read-permission on the removed
comment|// childNPath, no corresponding event must be generated.
name|Event
index|[]
name|evts
init|=
name|listener
operator|.
name|getEvents
argument_list|(
name|DEFAULT_WAIT_TIMEOUT
argument_list|)
decl_stmt|;
for|for
control|(
name|Event
name|evt
range|:
name|evts
control|)
block|{
if|if
condition|(
name|evt
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|NODE_REMOVED
operator|&&
name|evt
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|childNPath
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"TestUser does not have READ permission below "
operator|+
name|path
operator|+
literal|" -> events below must not show up."
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|obsMgr
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-4196">OAK-4196</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testEventRemovedNodeWhenDenyEntryIsRemoved
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw the READ privilege on childNPath
name|deny
argument_list|(
name|childNPath
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath2
argument_list|)
argument_list|)
expr_stmt|;
comment|// testUser registers a event listener for changes under testRoot
name|ObservationManager
name|obsMgr
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|EventResult
name|listener
init|=
operator|new
name|EventResult
argument_list|(
name|this
operator|.
name|log
argument_list|)
decl_stmt|;
try|try
block|{
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|Event
operator|.
name|NODE_REMOVED
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// superuser removes the node with childNPath& childNPath2 in
comment|// order to provoke events being generated
name|superuser
operator|.
name|getItem
argument_list|(
name|childNPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|getItem
argument_list|(
name|childNPath2
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// since the events are generated _after_ persisting all the changes
comment|// and the removal also removes the permission entries denying access
comment|// testUser will be notified about the removal because the remaining
comment|// permission setup after the removal grants read access.
name|Event
index|[]
name|evts
init|=
name|listener
operator|.
name|getEvents
argument_list|(
name|DEFAULT_WAIT_TIMEOUT
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|eventPaths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|Event
name|evt
range|:
name|evts
control|)
block|{
if|if
condition|(
name|evt
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|NODE_REMOVED
condition|)
block|{
name|eventPaths
operator|.
name|add
argument_list|(
name|evt
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|assertTrue
argument_list|(
name|eventPaths
operator|.
name|contains
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|eventPaths
operator|.
name|contains
argument_list|(
name|childNPath2
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|obsMgr
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * @see<a href="https://issues.apache.org/jira/browse/OAK-4196">OAK-4196</a>      */
annotation|@
name|Test
specifier|public
name|void
name|testEventRemovedNode
parameter_list|()
throws|throws
name|Exception
block|{
comment|// withdraw the READ privilege on childNPath
name|deny
argument_list|(
name|path
argument_list|,
name|readPrivileges
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|testSession
operator|.
name|nodeExists
argument_list|(
name|childNPath
argument_list|)
argument_list|)
expr_stmt|;
comment|// testUser registers a event listener for changes under testRoot
name|ObservationManager
name|obsMgr
init|=
name|testSession
operator|.
name|getWorkspace
argument_list|()
operator|.
name|getObservationManager
argument_list|()
decl_stmt|;
name|EventResult
name|listener
init|=
operator|new
name|EventResult
argument_list|(
name|this
operator|.
name|log
argument_list|)
decl_stmt|;
try|try
block|{
name|obsMgr
operator|.
name|addEventListener
argument_list|(
name|listener
argument_list|,
name|Event
operator|.
name|NODE_REMOVED
argument_list|,
name|testRoot
argument_list|,
literal|true
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|true
argument_list|)
expr_stmt|;
comment|// superuser removes the node with childNPath order to provoke events being generated
name|superuser
operator|.
name|getItem
argument_list|(
name|childNPath
argument_list|)
operator|.
name|remove
argument_list|()
expr_stmt|;
name|superuser
operator|.
name|save
argument_list|()
expr_stmt|;
comment|// since the testUser does not have read-permission on the removed
comment|// childNPath, no corresponding event must be generated.
name|Event
index|[]
name|evts
init|=
name|listener
operator|.
name|getEvents
argument_list|(
name|DEFAULT_WAIT_TIMEOUT
argument_list|)
decl_stmt|;
for|for
control|(
name|Event
name|evt
range|:
name|evts
control|)
block|{
if|if
condition|(
name|evt
operator|.
name|getType
argument_list|()
operator|==
name|Event
operator|.
name|NODE_REMOVED
operator|&&
name|evt
operator|.
name|getPath
argument_list|()
operator|.
name|equals
argument_list|(
name|childNPath
argument_list|)
condition|)
block|{
name|fail
argument_list|(
literal|"TestUser does not have READ permission on "
operator|+
name|childNPath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
name|obsMgr
operator|.
name|removeEventListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

