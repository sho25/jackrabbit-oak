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
name|commit
operator|.
name|EmptyHook
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
name|NodeBuilder
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
name|stats
operator|.
name|Clock
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotSame
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_class
specifier|public
class|class
name|CheckpointsTest
block|{
specifier|private
specifier|final
name|Clock
name|clock
init|=
operator|new
name|Clock
operator|.
name|Virtual
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|store
init|=
operator|new
name|DocumentMK
operator|.
name|Builder
argument_list|()
operator|.
name|clock
argument_list|(
name|clock
argument_list|)
operator|.
name|getNodeStore
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testCheckpointPurge
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|expiryTime
init|=
literal|1000
decl_stmt|;
name|Revision
name|r1
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|checkpoint
argument_list|(
name|expiryTime
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|r1
argument_list|,
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
argument_list|)
expr_stmt|;
comment|//Trigger expiry by forwarding the clock to future
name|clock
operator|.
name|waitUntil
argument_list|(
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|expiryTime
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testGetOldestRevisionToKeep
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|et1
init|=
literal|1000
decl_stmt|,
name|et2
init|=
name|et1
operator|+
literal|1000
decl_stmt|;
name|Revision
name|r1
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|checkpoint
argument_list|(
name|et1
argument_list|)
argument_list|)
decl_stmt|;
comment|//Do some commit to change headRevision
name|NodeBuilder
name|b2
init|=
name|store
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|b2
operator|.
name|child
argument_list|(
literal|"x"
argument_list|)
expr_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|b2
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|Revision
name|r2
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|store
operator|.
name|checkpoint
argument_list|(
name|et2
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotSame
argument_list|(
name|r1
argument_list|,
name|r2
argument_list|)
expr_stmt|;
comment|//r2 has the later expiry
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|starttime
init|=
name|clock
operator|.
name|getTime
argument_list|()
decl_stmt|;
comment|//Trigger expiry by forwarding the clock to future e1
name|clock
operator|.
name|waitUntil
argument_list|(
name|starttime
operator|+
name|et1
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|r2
argument_list|,
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
argument_list|)
expr_stmt|;
comment|//Trigger expiry by forwarding the clock to future e2
comment|//This time no valid checkpoint
name|clock
operator|.
name|waitUntil
argument_list|(
name|starttime
operator|+
name|et2
operator|+
literal|1
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|store
operator|.
name|getCheckpoints
argument_list|()
operator|.
name|getOldestRevisionToKeep
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

