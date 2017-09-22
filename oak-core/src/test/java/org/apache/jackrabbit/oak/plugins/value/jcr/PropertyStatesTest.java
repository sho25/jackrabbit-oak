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
name|value
operator|.
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|PropertyType
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
name|javax
operator|.
name|jcr
operator|.
name|Value
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
name|api
operator|.
name|PropertyState
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
name|api
operator|.
name|Type
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
name|namepath
operator|.
name|impl
operator|.
name|LocalNameMapper
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
name|namepath
operator|.
name|impl
operator|.
name|NamePathMapperImpl
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
name|memory
operator|.
name|PropertyStates
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
name|util
operator|.
name|ISO8601
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
name|java
operator|.
name|util
operator|.
name|Collections
operator|.
name|singletonMap
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

begin_class
specifier|public
class|class
name|PropertyStatesTest
block|{
specifier|private
specifier|final
name|NamePathMapperImpl
name|namePathMapper
init|=
operator|new
name|NamePathMapperImpl
argument_list|(
operator|new
name|LocalNameMapper
argument_list|(
name|singletonMap
argument_list|(
literal|"oak-prefix"
argument_list|,
literal|"http://jackrabbit.apache.org"
argument_list|)
argument_list|,
name|singletonMap
argument_list|(
literal|"jcr-prefix"
argument_list|,
literal|"http://jackrabbit.apache.org"
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|namePropertyFromNameValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyState
name|nameProperty
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"name"
argument_list|,
literal|"oak-prefix:value"
argument_list|,
name|PropertyType
operator|.
name|NAME
argument_list|)
decl_stmt|;
name|Value
name|nameValue
init|=
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|nameProperty
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|PropertyState
name|namePropertyFromValue
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"name"
argument_list|,
name|nameValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|nameProperty
argument_list|,
name|namePropertyFromValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|pathPropertyFromPathValue
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|PropertyState
name|pathProperty
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"path"
argument_list|,
literal|"oak-prefix:a/oak-prefix:b"
argument_list|,
name|PropertyType
operator|.
name|PATH
argument_list|)
decl_stmt|;
name|Value
name|nameValue
init|=
name|ValueFactoryImpl
operator|.
name|createValue
argument_list|(
name|pathProperty
argument_list|,
name|namePathMapper
argument_list|)
decl_stmt|;
name|PropertyState
name|namePropertyFromValue
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"path"
argument_list|,
name|nameValue
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|pathProperty
argument_list|,
name|namePropertyFromValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|dateValueFromDateProperty
parameter_list|()
throws|throws
name|RepositoryException
block|{
name|String
name|expected
init|=
name|ISO8601
operator|.
name|format
argument_list|(
name|Calendar
operator|.
name|getInstance
argument_list|()
argument_list|)
decl_stmt|;
name|PropertyState
name|dateProperty
init|=
name|PropertyStates
operator|.
name|createProperty
argument_list|(
literal|"date"
argument_list|,
name|expected
argument_list|,
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|String
name|actual
init|=
name|dateProperty
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|actual
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

