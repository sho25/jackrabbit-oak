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
name|assertFalse
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
name|plugins
operator|.
name|document
operator|.
name|impl
operator|.
name|SimpleNodeScenario
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
comment|/**  * Tests for {@code MongoMicroKernel#getHeadRevision()}.  */
end_comment

begin_class
specifier|public
class|class
name|DocumentMKGetHeadRevisionTest
extends|extends
name|BaseDocumentMKTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|simple
parameter_list|()
throws|throws
name|Exception
block|{
name|SimpleNodeScenario
name|scenario
init|=
operator|new
name|SimpleNodeScenario
argument_list|(
name|mk
argument_list|)
decl_stmt|;
name|String
name|rev1
init|=
name|scenario
operator|.
name|create
argument_list|()
decl_stmt|;
name|String
name|rev2
init|=
name|mk
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|rev1
argument_list|,
name|rev2
argument_list|)
expr_stmt|;
name|String
name|rev3
init|=
name|scenario
operator|.
name|deleteA
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|rev3
operator|.
name|equals
argument_list|(
name|rev2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

