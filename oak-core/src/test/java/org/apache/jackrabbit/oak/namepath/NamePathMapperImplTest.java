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
name|HashMap
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

begin_class
specifier|public
class|class
name|NamePathMapperImplTest
block|{
specifier|private
name|TestNameMapper
name|mapper
init|=
operator|new
name|TestNameMapper
argument_list|(
literal|true
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
name|testNullName
parameter_list|()
block|{
name|assertNull
argument_list|(
name|npMapper
operator|.
name|getJcrName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|npMapper
operator|.
name|getOakName
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyName
parameter_list|()
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
name|TestNameMapper
name|mapper
init|=
operator|new
name|TestNameMapper
argument_list|(
literal|false
argument_list|)
decl_stmt|;
comment|// a mapper with no prefix remappings present
name|NamePathMapper
name|npMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
name|mapper
argument_list|)
decl_stmt|;
name|checkIdentical
argument_list|(
name|npMapper
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
name|checkIdentical
argument_list|(
name|npMapper
argument_list|,
literal|"/foo:bar"
argument_list|)
expr_stmt|;
name|checkIdentical
argument_list|(
name|npMapper
argument_list|,
literal|"/foo:bar/quu:qux"
argument_list|)
expr_stmt|;
name|checkIdentical
argument_list|(
name|npMapper
argument_list|,
literal|"foo:bar"
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
literal|"/jcr-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"/foo:bar"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/jcr-foo:bar/jcr-quu:qux"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"/foo:bar/quu:qux"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"jcr-foo:bar"
argument_list|,
name|npMapper
operator|.
name|getJcrPath
argument_list|(
literal|"foo:bar"
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
name|IllegalStateException
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
name|IllegalStateException
name|expected
parameter_list|)
block|{         }
block|}
specifier|private
name|void
name|checkEquals
parameter_list|(
name|NamePathMapper
name|npMapper
parameter_list|,
name|String
name|jcrPath
parameter_list|)
block|{
name|String
name|oakPath
init|=
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|jcrPath
argument_list|,
name|oakPath
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|checkIdentical
parameter_list|(
name|NamePathMapper
name|npMapper
parameter_list|,
name|String
name|jcrPath
parameter_list|)
block|{
name|String
name|oakPath
init|=
name|npMapper
operator|.
name|getOakPathKeepIndex
argument_list|(
name|jcrPath
argument_list|)
decl_stmt|;
name|checkIdentical
argument_list|(
name|jcrPath
argument_list|,
name|oakPath
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|checkIdentical
parameter_list|(
name|String
name|expected
parameter_list|,
name|String
name|actual
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
if|if
condition|(
name|expected
operator|!=
name|actual
condition|)
block|{
name|fail
argument_list|(
literal|"Expected the strings to be the same"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|TestNameMapper
extends|extends
name|AbstractNameMapper
block|{
specifier|private
name|boolean
name|withRemappings
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|uri2oakprefix
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|TestNameMapper
parameter_list|(
name|boolean
name|withRemappings
parameter_list|)
block|{
name|this
operator|.
name|withRemappings
operator|=
name|withRemappings
expr_stmt|;
name|uri2oakprefix
operator|.
name|put
argument_list|(
literal|""
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|uri2oakprefix
operator|.
name|put
argument_list|(
literal|"http://www.jcp.org/jcr/1.0"
argument_list|,
literal|"jcr"
argument_list|)
expr_stmt|;
name|uri2oakprefix
operator|.
name|put
argument_list|(
literal|"http://www.jcp.org/jcr/nt/1.0"
argument_list|,
literal|"nt"
argument_list|)
expr_stmt|;
name|uri2oakprefix
operator|.
name|put
argument_list|(
literal|"http://www.jcp.org/jcr/mix/1.0"
argument_list|,
literal|"mix"
argument_list|)
expr_stmt|;
name|uri2oakprefix
operator|.
name|put
argument_list|(
literal|"http://www.w3.org/XML/1998/namespace"
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getJcrPrefix
parameter_list|(
name|String
name|oakPrefix
parameter_list|)
block|{
if|if
condition|(
name|oakPrefix
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|withRemappings
condition|)
block|{
return|return
name|oakPrefix
return|;
block|}
else|else
block|{
return|return
literal|"jcr-"
operator|+
name|oakPrefix
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getOakPrefix
parameter_list|(
name|String
name|jcrPrefix
parameter_list|)
block|{
if|if
condition|(
name|jcrPrefix
operator|.
name|isEmpty
argument_list|()
operator|||
operator|!
name|withRemappings
condition|)
block|{
return|return
name|jcrPrefix
return|;
block|}
else|else
block|{
return|return
literal|"oak-"
operator|+
name|jcrPrefix
return|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|String
name|getOakPrefixFromURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
return|return
operator|(
name|withRemappings
condition|?
literal|"oak-"
else|:
literal|""
operator|)
operator|+
name|uri2oakprefix
operator|.
name|get
argument_list|(
name|uri
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasSessionLocalMappings
parameter_list|()
block|{
return|return
name|withRemappings
return|;
block|}
block|}
block|}
end_class

end_unit

