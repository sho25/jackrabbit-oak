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
name|junit
operator|.
name|Before
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Rule
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
name|assertNotNull
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
name|VersionGCInitTest
block|{
annotation|@
name|Rule
specifier|public
specifier|final
name|DocumentMKBuilderProvider
name|builderProvider
init|=
operator|new
name|DocumentMKBuilderProvider
argument_list|()
decl_stmt|;
specifier|private
name|DocumentNodeStore
name|ns
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|before
parameter_list|()
block|{
name|ns
operator|=
name|builderProvider
operator|.
name|newBuilder
argument_list|()
operator|.
name|getNodeStore
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|lazyInitialize
parameter_list|()
throws|throws
name|Exception
block|{
name|DocumentStore
name|store
init|=
name|ns
operator|.
name|getDocumentStore
argument_list|()
decl_stmt|;
name|Document
name|vgc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
literal|"versionGC"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|vgc
argument_list|)
expr_stmt|;
name|ns
operator|.
name|getVersionGarbageCollector
argument_list|()
expr_stmt|;
name|vgc
operator|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
literal|"versionGC"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|vgc
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

