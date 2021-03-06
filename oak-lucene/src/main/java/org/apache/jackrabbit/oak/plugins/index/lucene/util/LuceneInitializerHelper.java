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
name|lucene
operator|.
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|search
operator|.
name|util
operator|.
name|IndexHelper
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
name|spi
operator|.
name|lifecycle
operator|.
name|RepositoryInitializer
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkNotNull
import|;
end_import

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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
import|;
end_import

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
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLuceneFileIndexDefinition
import|;
end_import

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
name|lucene
operator|.
name|util
operator|.
name|LuceneIndexHelper
operator|.
name|newLuceneIndexDefinition
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneInitializerHelper
implements|implements
name|RepositoryInitializer
block|{
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
decl_stmt|;
specifier|private
specifier|final
name|String
name|filePath
decl_stmt|;
specifier|private
name|String
name|async
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|storageEnabled
decl_stmt|;
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|IndexHelper
operator|.
name|JR_PROPERTY_INCLUDES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|,
name|Boolean
name|storageEnabled
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|IndexHelper
operator|.
name|JR_PROPERTY_INCLUDES
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|storageEnabled
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|propertyTypes
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|propertyTypes
argument_list|,
name|excludes
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
name|String
name|filePath
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|,
name|propertyTypes
argument_list|,
literal|null
argument_list|,
name|filePath
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneInitializerHelper
parameter_list|(
name|String
name|name
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|propertyTypes
parameter_list|,
name|Set
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
name|String
name|filePath
parameter_list|,
name|Boolean
name|storageEnabled
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|propertyTypes
operator|=
name|propertyTypes
expr_stmt|;
name|this
operator|.
name|excludes
operator|=
name|excludes
expr_stmt|;
name|this
operator|.
name|filePath
operator|=
name|filePath
expr_stmt|;
name|this
operator|.
name|storageEnabled
operator|=
name|storageEnabled
expr_stmt|;
block|}
comment|/**      * set the {@code async} property to "async".      * @return      */
specifier|public
name|LuceneInitializerHelper
name|async
parameter_list|()
block|{
return|return
name|async
argument_list|(
literal|"async"
argument_list|)
return|;
block|}
comment|/**      * will set the {@code async} property to the provided value      *      * @param async      * @return      */
specifier|public
name|LuceneInitializerHelper
name|async
parameter_list|(
annotation|@
name|NotNull
specifier|final
name|String
name|async
parameter_list|)
block|{
name|this
operator|.
name|async
operator|=
name|checkNotNull
argument_list|(
name|async
argument_list|)
expr_stmt|;
return|return
name|this
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
annotation|@
name|NotNull
name|NodeBuilder
name|builder
parameter_list|)
block|{
if|if
condition|(
name|builder
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|&&
name|builder
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
comment|// do nothing
block|}
elseif|else
if|if
condition|(
name|filePath
operator|==
literal|null
condition|)
block|{
name|newLuceneIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
name|name
argument_list|,
name|propertyTypes
argument_list|,
name|excludes
argument_list|,
name|async
argument_list|,
name|storageEnabled
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|newLuceneFileIndexDefinition
argument_list|(
name|builder
operator|.
name|child
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
argument_list|,
name|name
argument_list|,
name|propertyTypes
argument_list|,
name|excludes
argument_list|,
name|filePath
argument_list|,
name|async
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

