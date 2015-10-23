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
name|api
package|;
end_package

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

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runners
operator|.
name|Parameterized
operator|.
name|Parameters
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
annotation|@
name|RunWith
argument_list|(
name|Parameterized
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TypePreconditionTest
block|{
annotation|@
name|Parameters
argument_list|(
name|name
operator|=
literal|"{0}/{1}"
argument_list|)
specifier|public
specifier|static
name|Object
index|[]
index|[]
name|getTypes
parameter_list|()
block|{
return|return
operator|new
name|Object
index|[]
index|[]
block|{
block|{
name|Type
operator|.
name|BINARIES
block|,
name|Type
operator|.
name|BINARY
block|}
block|,
block|{
name|Type
operator|.
name|BOOLEANS
block|,
name|Type
operator|.
name|BOOLEAN
block|}
block|,
block|{
name|Type
operator|.
name|DATES
block|,
name|Type
operator|.
name|DATE
block|}
block|,
block|{
name|Type
operator|.
name|DECIMALS
block|,
name|Type
operator|.
name|DECIMAL
block|}
block|,
block|{
name|Type
operator|.
name|DOUBLES
block|,
name|Type
operator|.
name|DOUBLE
block|}
block|,
block|{
name|Type
operator|.
name|LONGS
block|,
name|Type
operator|.
name|LONG
block|}
block|,
block|{
name|Type
operator|.
name|NAMES
block|,
name|Type
operator|.
name|NAME
block|}
block|,
block|{
name|Type
operator|.
name|PATHS
block|,
name|Type
operator|.
name|PATH
block|}
block|,
block|{
name|Type
operator|.
name|REFERENCES
block|,
name|Type
operator|.
name|REFERENCE
block|}
block|,
block|{
name|Type
operator|.
name|STRINGS
block|,
name|Type
operator|.
name|STRING
block|}
block|,
block|{
name|Type
operator|.
name|UNDEFINEDS
block|,
name|Type
operator|.
name|UNDEFINED
block|}
block|,
block|{
name|Type
operator|.
name|URIS
block|,
name|Type
operator|.
name|URI
block|}
block|,
block|{
name|Type
operator|.
name|WEAKREFERENCES
block|,
name|Type
operator|.
name|WEAKREFERENCE
block|}
block|}
return|;
block|}
specifier|private
specifier|final
name|Type
name|multi
decl_stmt|;
specifier|private
specifier|final
name|Type
name|single
decl_stmt|;
specifier|public
name|TypePreconditionTest
parameter_list|(
name|Type
name|multi
parameter_list|,
name|Type
name|single
parameter_list|)
block|{
name|this
operator|.
name|multi
operator|=
name|multi
expr_stmt|;
name|this
operator|.
name|single
operator|=
name|single
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBaseTypeOnMultiValueType
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|single
argument_list|,
name|multi
operator|.
name|getBaseType
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testBaseTypeOnSingleValueType
parameter_list|()
block|{
name|single
operator|.
name|getBaseType
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IllegalStateException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|testArrayTypeOnMultiValueType
parameter_list|()
block|{
name|multi
operator|.
name|getArrayType
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testArrayTypeOnSingleValueType
parameter_list|()
block|{
name|assertEquals
argument_list|(
name|multi
argument_list|,
name|single
operator|.
name|getArrayType
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

