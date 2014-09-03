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
name|jcr
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * helper class that return the list of available fixtures based on the {@code ns-fixtures} system  * property ({@code -Dns-fixtures=SEGMENT_MK}).  *   * See {@link FixturesHelper#Fixture} for a list of available fixtures  */
end_comment

begin_class
specifier|public
class|class
name|FixturesHelper
block|{
comment|/**      * splitter for specifying multiple fixtures      */
specifier|private
specifier|static
specifier|final
name|String
name|SPLIT_ON
init|=
literal|","
decl_stmt|;
comment|/**      * System property to be used.      */
specifier|public
specifier|static
specifier|final
name|String
name|NS_FIXTURES
init|=
literal|"ns-fixtures"
decl_stmt|;
comment|/**      * default fixtures when no {@code ns-fixtures} is provided      */
specifier|public
specifier|static
enum|enum
name|Fixture
block|{
name|DOCUMENT_MK
block|,
name|DOCUMENT_NS
block|,
name|SEGMENT_MK
block|,
name|DOCUMENT_JDBC
block|}
empty_stmt|;
specifier|private
specifier|static
specifier|final
name|Set
argument_list|<
name|Fixture
argument_list|>
name|FIXTURES
decl_stmt|;
static|static
block|{
name|String
name|raw
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|NS_FIXTURES
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|raw
operator|.
name|trim
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|FIXTURES
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Fixture
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Fixture
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
index|[]
name|fs
init|=
name|raw
operator|.
name|split
argument_list|(
name|SPLIT_ON
argument_list|)
decl_stmt|;
name|Set
argument_list|<
name|Fixture
argument_list|>
name|tmp
init|=
operator|new
name|HashSet
argument_list|<
name|Fixture
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|fs
control|)
block|{
name|String
name|x
init|=
name|f
operator|.
name|trim
argument_list|()
decl_stmt|;
name|Fixture
name|fx
init|=
name|Fixture
operator|.
name|valueOf
argument_list|(
name|x
operator|.
name|toUpperCase
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fx
operator|!=
literal|null
condition|)
block|{
name|tmp
operator|.
name|add
argument_list|(
name|fx
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tmp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|FIXTURES
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
operator|new
name|HashSet
argument_list|<
name|Fixture
argument_list|>
argument_list|(
name|Arrays
operator|.
name|asList
argument_list|(
name|Fixture
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|FIXTURES
operator|=
name|Collections
operator|.
name|unmodifiableSet
argument_list|(
name|tmp
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|static
name|Set
argument_list|<
name|Fixture
argument_list|>
name|getFixtures
parameter_list|()
block|{
return|return
name|FIXTURES
return|;
block|}
block|}
end_class

end_unit

