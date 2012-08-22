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
name|mk
operator|.
name|wrapper
package|;
end_package

begin_import
import|import static
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
name|Indexer
operator|.
name|INDEX_CONFIG_PATH
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
name|assertNull
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
name|mk
operator|.
name|api
operator|.
name|MicroKernel
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
name|mk
operator|.
name|index
operator|.
name|IndexWrapper
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
name|mk
operator|.
name|simple
operator|.
name|SimpleKernelImpl
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
comment|/**  * Test the index wrapper.  */
end_comment

begin_class
specifier|public
class|class
name|IndexWrapperTest
block|{
comment|// TODO: Remove SimpleKernelImpl-specific assumptions from the test
specifier|private
specifier|final
name|MicroKernel
name|mk
init|=
operator|new
name|IndexWrapper
argument_list|(
operator|new
name|SimpleKernelImpl
argument_list|(
literal|"mem:IndexWrapperTest"
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|String
name|head
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|getNodes
parameter_list|()
block|{
name|assertNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/unknown"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|mk
operator|.
name|getNodes
argument_list|(
literal|"/unknown"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|prefix
parameter_list|()
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
name|INDEX_CONFIG_PATH
argument_list|,
literal|"+ \"prefix@x\": {}"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n1\": { \"value\":\"a:no\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n2\": { \"value\":\"x:yes\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n3\": { \"value\":\"x:a\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n4\": { \"value\":\"x:a\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|empty
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/prefix@x?x:no"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|empty
argument_list|)
expr_stmt|;
name|String
name|yes
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/prefix@x?x:yes"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[\"/n2/value\"]"
argument_list|,
name|yes
argument_list|)
expr_stmt|;
name|String
name|a
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/prefix@x?x:a"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[\"/n3/value\",\"/n4/value\"]"
argument_list|,
name|a
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyUnique
parameter_list|()
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
name|INDEX_CONFIG_PATH
argument_list|,
literal|"+ \"property@id,unique\": {}"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n1\": { \"value\":\"empty\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n2\": { \"id\":\"1\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|empty
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/property@id,unique?0"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|empty
argument_list|)
expr_stmt|;
name|String
name|one
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/property@id,unique?1"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[\"/n2\"]"
argument_list|,
name|one
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|propertyNonUnique
parameter_list|()
block|{
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
name|INDEX_CONFIG_PATH
argument_list|,
literal|"+ \"property@ref\": {}"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n1\": { \"ref\":\"a\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n2\": { \"ref\":\"b\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/"
argument_list|,
literal|"+ \"n3\": { \"ref\":\"b\" }"
argument_list|,
name|head
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|empty
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/property@ref?no"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[]"
argument_list|,
name|empty
argument_list|)
expr_stmt|;
name|String
name|one
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/property@ref?a"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[\"/n1\"]"
argument_list|,
name|one
argument_list|)
expr_stmt|;
name|String
name|two
init|=
name|mk
operator|.
name|getNodes
argument_list|(
name|INDEX_CONFIG_PATH
operator|+
literal|"/property@ref?b"
argument_list|,
name|head
argument_list|,
literal|1
argument_list|,
literal|0
argument_list|,
operator|-
literal|1
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"[\"/n2\",\"/n3\"]"
argument_list|,
name|two
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

