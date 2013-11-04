begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|namepath
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|RepositoryException
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|ImmutableMap
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
name|TestGlobalNameMapper
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
name|TestNameMapper
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
name|identifier
operator|.
name|IdentifierManager
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

begin_class
specifier|public
class|class
name|NamePathMapperImplTest
block|{
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|GLOBAL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"oak-jcr"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|,
literal|"oak-nt"
argument_list|,
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|,
literal|"oak-mix"
argument_list|,
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|,
literal|"oak-foo"
argument_list|,
literal|"http://www.example.com/foo"
argument_list|,
literal|"oak-quu"
argument_list|,
literal|"http://www.example.com/quu"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|LOCAL
init|=
name|ImmutableMap
operator|.
name|of
argument_list|(
literal|"jcr-jcr"
argument_list|,
literal|"http://www.jcp.org/jcr/1.0"
argument_list|,
literal|"jcr-nt"
argument_list|,
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|,
literal|"jcr-mix"
argument_list|,
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|,
literal|"foo"
argument_list|,
literal|"http://www.example.com/foo"
argument_list|,
literal|"quu"
argument_list|,
literal|"http://www.example.com/quu"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NameMapper
name|mapper
init|=
operator|new
name|TestNameMapper
argument_list|(
name|GLOBAL
argument_list|,
name|LOCAL
argument_list|)
decl_stmt|;
specifier|private
name|NamePathMapper
name|npMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testInvalidIdentifierPath
parameter_list|()
block|{
name|String
name|uuid
init|=
name|IdentifierManager
operator|.
name|generateUUID
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|invalid
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|'['
operator|+
name|uuid
operator|+
literal|"]abc"
argument_list|)
expr_stmt|;
name|invalid
operator|.
name|add
argument_list|(
literal|'['
operator|+
name|uuid
operator|+
literal|"]/a/b/c"
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|jcrPath
range|:
name|invalid
control|)
block|{
name|assertNull
argument_list|(
name|npMapper
operator|.
name|getOakPath
argument_list|(
name|jcrPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyName
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getJcrName
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakNameOrNull
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakName
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testTrailingSlash
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/oak-foo:bar/oak-quu:qux"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/foo:bar/quu:qux/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b/c"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/a/b/c/"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrToOak
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"{}foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/oak-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/oak-foo:bar/oak-quu:qux"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-nt:unstructured"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"{http://www.jcp.org/jcr/nt/1.0}unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".."
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"../.."
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../../../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"../oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/../../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".."
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|".."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/./."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/./{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"foobar/./../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b/c"
argument_list|,
name|npMapper
operator|.
name|getOakPath
argument_list|(
literal|"/a/b[1]/c[01]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrToOakKeepIndex
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"{}foo"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/oak-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/oak-foo:bar/oak-quu:qux"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-nt:unstructured"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"{http://www.jcp.org/jcr/nt/1.0}unstructured"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".."
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"../.."
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/../../../.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"../oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/../../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|".."
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|".."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|""
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content/./."
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foobar/oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/./{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"oak-jcr:content"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foobar/./../{http://www.jcp.org/jcr/1.0}content"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/a/b[1]/c[1]"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/a/b[1]/c[01]"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testJcrToOakKeepIndexNoRemap
parameter_list|()
block|{
name|NameMapper
name|mapper
init|=
operator|new
name|TestGlobalNameMapper
argument_list|(
name|GLOBAL
argument_list|)
decl_stmt|;
name|NamePathMapper
name|npMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"/"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo:bar"
argument_list|,
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
literal|"foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testOakToJcr
parameter_list|()
block|{
name|assertEquals
argument_list|(
literal|"/foo:bar"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"/oak-foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"/oak-foo:bar/oak-quu:qux"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"foo:bar"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"oak-foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"."
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|""
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"{http://www.jcp.org/jcr/nt/1.0}unstructured"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expanded name should not be accepted"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{         }
try|try
block|{
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"foobar/{http://www.jcp.org/jcr/1.0}content"
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"expanded name should not be accepted"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidJcrPaths
parameter_list|()
block|{
name|String
index|[]
name|paths
init|=
block|{
literal|"//"
block|,
literal|"/foo//"
block|,
literal|"/..//"
block|,
literal|"/.."
block|,
literal|"/foo/../.."
block|,
literal|"foo::bar"
block|,
literal|"foo:bar:baz"
block|}
decl_stmt|;
for|for
control|(
name|String
name|path
range|:
name|paths
control|)
block|{
name|assertNull
argument_list|(
name|npMapper
operator|.
name|getOakPath
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testInvalidOakPaths
parameter_list|()
block|{
name|getJcrPath
argument_list|(
literal|"//"
argument_list|)
expr_stmt|;
name|getJcrPath
argument_list|(
literal|"/foo//"
argument_list|)
expr_stmt|;
name|getJcrPath
argument_list|(
literal|"/..//"
argument_list|)
expr_stmt|;
name|getJcrPath
argument_list|(
literal|"/.."
argument_list|)
expr_stmt|;
name|getJcrPath
argument_list|(
literal|"/foo/../.."
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|getJcrPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
try|try
block|{
name|npMapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"Expected IllegalArgumentException"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|expected
parameter_list|)
block|{         }
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateInvalidPaths
parameter_list|()
block|{
name|assertFalse
argument_list|(
name|JcrPathParser
operator|.
name|validate
argument_list|(
literal|"//"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|JcrPathParser
operator|.
name|validate
argument_list|(
literal|"/foo//"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|JcrPathParser
operator|.
name|validate
argument_list|(
literal|"/..//"
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|JcrPathParser
operator|.
name|validate
argument_list|(
literal|"/.."
argument_list|)
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|JcrPathParser
operator|.
name|validate
argument_list|(
literal|"/foo/../.."
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBracketsInNodeName
parameter_list|()
throws|throws
name|Exception
block|{
name|String
index|[]
name|childNames
init|=
block|{
literal|"{A}"
block|,
literal|"B}"
block|,
literal|"{C"
block|,
literal|"(D)"
block|,
literal|"E)"
block|,
literal|"(F"
block|, }
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|childNames
control|)
block|{
name|assertEquals
argument_list|(
name|name
argument_list|,
name|npMapper
operator|.
name|getOakName
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

