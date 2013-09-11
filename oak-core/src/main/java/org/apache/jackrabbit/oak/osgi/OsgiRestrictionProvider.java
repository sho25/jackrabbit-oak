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
name|osgi
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
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
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
name|Tree
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|CompositeRestrictionProvider
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|Restriction
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionDefinition
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionPattern
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
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionProvider
import|;
end_import

begin_comment
comment|/**  * {@link RestrictionProvider} implementation that combines all available OSGi  * restriction providers.  */
end_comment

begin_class
specifier|public
class|class
name|OsgiRestrictionProvider
extends|extends
name|AbstractServiceTracker
argument_list|<
name|RestrictionProvider
argument_list|>
implements|implements
name|RestrictionProvider
block|{
specifier|public
name|OsgiRestrictionProvider
parameter_list|()
block|{
name|super
argument_list|(
name|RestrictionProvider
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|RestrictionDefinition
argument_list|>
name|getSupportedRestrictions
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
block|{
return|return
name|getProvider
argument_list|()
operator|.
name|getSupportedRestrictions
argument_list|(
name|oakPath
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Restriction
name|createRestriction
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|,
annotation|@
name|Nonnull
name|Value
name|value
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
block|{
return|return
name|getProvider
argument_list|()
operator|.
name|createRestriction
argument_list|(
name|oakPath
argument_list|,
name|oakName
argument_list|,
name|value
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Restriction
name|createRestriction
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|String
name|oakName
parameter_list|,
annotation|@
name|Nonnull
name|Value
modifier|...
name|values
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
block|{
return|return
name|getProvider
argument_list|()
operator|.
name|createRestriction
argument_list|(
name|oakPath
argument_list|,
name|oakName
argument_list|,
name|values
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Restriction
argument_list|>
name|readRestrictions
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|aceTree
parameter_list|)
block|{
return|return
name|getProvider
argument_list|()
operator|.
name|readRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|writeRestrictions
parameter_list|(
name|String
name|oakPath
parameter_list|,
name|Tree
name|aceTree
parameter_list|,
name|Set
argument_list|<
name|Restriction
argument_list|>
name|restrictions
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|getProvider
argument_list|()
operator|.
name|writeRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|,
name|restrictions
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|validateRestrictions
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|aceTree
parameter_list|)
throws|throws
name|AccessControlException
throws|,
name|RepositoryException
block|{
name|getProvider
argument_list|()
operator|.
name|validateRestrictions
argument_list|(
name|oakPath
argument_list|,
name|aceTree
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|RestrictionPattern
name|getPattern
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|getProvider
argument_list|()
operator|.
name|getPattern
argument_list|(
name|oakPath
argument_list|,
name|tree
argument_list|)
return|;
block|}
comment|//------------------------------------------------------------< private>---
specifier|private
name|RestrictionProvider
name|getProvider
parameter_list|()
block|{
return|return
name|CompositeRestrictionProvider
operator|.
name|newInstance
argument_list|(
name|getServices
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

