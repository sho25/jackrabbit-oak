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
name|api
operator|.
name|CommitFailedException
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
name|assertSame
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
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_class
specifier|public
class|class
name|ConflictExceptionTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|type
parameter_list|()
block|{
name|ConflictException
name|e
init|=
operator|new
name|ConflictException
argument_list|(
literal|"conflict"
argument_list|)
decl_stmt|;
name|CommitFailedException
name|cfe
init|=
name|e
operator|.
name|asCommitFailedException
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|CommitFailedException
operator|.
name|MERGE
argument_list|,
name|cfe
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|cause
parameter_list|()
block|{
name|ConflictException
name|e
init|=
operator|new
name|ConflictException
argument_list|(
literal|"conflict"
argument_list|)
decl_stmt|;
name|CommitFailedException
name|cfe
init|=
name|e
operator|.
name|asCommitFailedException
argument_list|()
decl_stmt|;
name|assertSame
argument_list|(
name|e
argument_list|,
name|cfe
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|asCommitFailedException
parameter_list|()
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|newRevision
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|ConflictException
name|e
init|=
operator|new
name|ConflictException
argument_list|(
literal|"conflict"
argument_list|,
name|r
argument_list|)
decl_stmt|;
name|CommitFailedException
name|cfe
init|=
name|e
operator|.
name|asCommitFailedException
argument_list|()
decl_stmt|;
name|assertTrue
argument_list|(
name|cfe
operator|instanceof
name|FailedWithConflictException
argument_list|)
expr_stmt|;
name|FailedWithConflictException
name|fwce
init|=
operator|(
name|FailedWithConflictException
operator|)
name|cfe
decl_stmt|;
name|assertEquals
argument_list|(
name|CommitFailedException
operator|.
name|MERGE
argument_list|,
name|fwce
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|Collections
operator|.
name|singleton
argument_list|(
name|r
argument_list|)
argument_list|,
name|fwce
operator|.
name|getConflictRevisions
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

