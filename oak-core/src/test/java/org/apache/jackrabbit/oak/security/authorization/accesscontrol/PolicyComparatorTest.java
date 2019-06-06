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
name|security
operator|.
name|authorization
operator|.
name|accesscontrol
package|;
end_package

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|primitives
operator|.
name|Ints
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
name|api
operator|.
name|security
operator|.
name|JackrabbitAccessControlPolicy
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
name|commons
operator|.
name|PathUtils
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
name|mockito
operator|.
name|Mockito
operator|.
name|mock
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|mockito
operator|.
name|Mockito
operator|.
name|when
import|;
end_import

begin_class
specifier|public
class|class
name|PolicyComparatorTest
block|{
specifier|private
specifier|final
name|PolicyComparator
name|comparator
init|=
operator|new
name|PolicyComparator
argument_list|()
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testSame
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy
init|=
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy
argument_list|,
name|policy
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEquals
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
operator|new
name|JackrabbitAccessControlPolicy
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
return|return
name|object
operator|instanceof
name|JackrabbitAccessControlPolicy
return|;
block|}
block|}
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
parameter_list|()
lambda|->
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
block|}
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullPath1
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
operator|-
literal|1
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testNullPath2
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|null
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualPath
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEqualDepth
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path1"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/path2"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
literal|"/some/path1"
operator|.
name|compareTo
argument_list|(
literal|"/some/path2"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPath1Deeper
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/some/deeper/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
name|Ints
operator|.
name|compare
argument_list|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
literal|"/some/deeper/path"
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getDepth
argument_list|(
literal|"/"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testPath2Deeper
parameter_list|()
block|{
name|JackrabbitAccessControlPolicy
name|policy1
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|JackrabbitAccessControlPolicy
name|policy2
init|=
name|when
argument_list|(
name|mock
argument_list|(
name|JackrabbitAccessControlPolicy
operator|.
name|class
argument_list|)
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|thenReturn
argument_list|(
literal|"/a/deeper/path"
argument_list|)
operator|.
name|getMock
argument_list|()
decl_stmt|;
name|int
name|expected
init|=
name|Ints
operator|.
name|compare
argument_list|(
name|PathUtils
operator|.
name|getDepth
argument_list|(
literal|"/path"
argument_list|)
argument_list|,
name|PathUtils
operator|.
name|getDepth
argument_list|(
literal|"/a/deeper/path"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|comparator
operator|.
name|compare
argument_list|(
name|policy1
argument_list|,
name|policy2
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

