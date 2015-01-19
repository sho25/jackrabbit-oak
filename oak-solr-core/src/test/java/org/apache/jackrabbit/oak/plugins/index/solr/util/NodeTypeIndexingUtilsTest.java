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
name|index
operator|.
name|solr
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|core
operator|.
name|query
operator|.
name|AbstractQueryTest
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
name|index
operator|.
name|solr
operator|.
name|util
operator|.
name|NodeTypeIndexingUtils
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
comment|/**  * Testcase for {@link org.apache.jackrabbit.oak.plugins.index.solr.util.NodeTypeIndexingUtils}  */
end_comment

begin_class
specifier|public
class|class
name|NodeTypeIndexingUtilsTest
extends|extends
name|AbstractQueryTest
block|{
specifier|public
name|void
name|testSynonymsFileCreation
parameter_list|()
throws|throws
name|Exception
block|{
name|File
name|synonymsFile
init|=
name|NodeTypeIndexingUtils
operator|.
name|createPrimaryTypeSynonymsFile
argument_list|(
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getFile
argument_list|()
operator|+
literal|"/pt-synonyms.txt"
argument_list|,
name|superuser
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|synonymsFile
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|synonymsFile
operator|.
name|exists
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

