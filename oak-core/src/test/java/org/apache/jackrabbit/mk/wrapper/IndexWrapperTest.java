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
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|MultiMkTestBase
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
import|;
end_import

begin_comment
comment|/**  * Test the index wrapper.  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|IndexWrapperTest
extends|extends
name|MultiMkTestBase
block|{
specifier|private
name|String
name|head
decl_stmt|;
specifier|public
name|IndexWrapperTest
parameter_list|(
name|String
name|url
parameter_list|)
block|{
name|super
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|mk
operator|=
operator|new
name|IndexWrapper
argument_list|(
name|mk
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
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
return|return;
block|}
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/index"
argument_list|,
literal|"+ \"prefix:x\": {}"
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
literal|"/index/prefix:x?x:no"
argument_list|,
name|head
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
literal|"/index/prefix:x?x:yes"
argument_list|,
name|head
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
literal|"/index/prefix:x?x:a"
argument_list|,
name|head
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
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
return|return;
block|}
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/index"
argument_list|,
literal|"+ \"property:id,unique\": {}"
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
literal|"/index/property:id,unique?0"
argument_list|,
name|head
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
literal|"/index/property:id,unique?1"
argument_list|,
name|head
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
if|if
condition|(
operator|!
name|isSimpleKernel
argument_list|(
name|mk
argument_list|)
condition|)
block|{
return|return;
block|}
name|head
operator|=
name|mk
operator|.
name|commit
argument_list|(
literal|"/index"
argument_list|,
literal|"+ \"property:ref\": {}"
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
literal|"/index/property:ref?no"
argument_list|,
name|head
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
literal|"/index/property:ref?a"
argument_list|,
name|head
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
literal|"/index/property:ref?b"
argument_list|,
name|head
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

