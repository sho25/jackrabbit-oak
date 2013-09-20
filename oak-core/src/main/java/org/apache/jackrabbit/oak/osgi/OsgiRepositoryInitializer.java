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
name|osgi
package|;
end_package

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
name|CompositeInitializer
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
name|osgi
operator|.
name|framework
operator|.
name|ServiceReference
import|;
end_import

begin_comment
comment|/**  * Implements a service tracker that keeps track of all  * {@link RepositoryInitializer}s in the system and calls the available  * method once the micro kernel is available.  */
end_comment

begin_class
specifier|public
class|class
name|OsgiRepositoryInitializer
extends|extends
name|AbstractServiceTracker
argument_list|<
name|RepositoryInitializer
argument_list|>
implements|implements
name|RepositoryInitializer
block|{
specifier|private
name|RepositoryInitializerObserver
name|observer
decl_stmt|;
specifier|public
name|OsgiRepositoryInitializer
parameter_list|()
block|{
name|super
argument_list|(
name|RepositoryInitializer
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|initialize
parameter_list|(
name|NodeBuilder
name|builder
parameter_list|)
block|{
operator|new
name|CompositeInitializer
argument_list|(
name|getServices
argument_list|()
argument_list|)
operator|.
name|initialize
argument_list|(
name|builder
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|addingService
parameter_list|(
name|ServiceReference
name|reference
parameter_list|)
block|{
name|RepositoryInitializer
name|ri
init|=
operator|(
name|RepositoryInitializer
operator|)
name|super
operator|.
name|addingService
argument_list|(
name|reference
argument_list|)
decl_stmt|;
if|if
condition|(
name|observer
operator|!=
literal|null
condition|)
block|{
name|observer
operator|.
name|newRepositoryInitializer
argument_list|(
name|ri
argument_list|)
expr_stmt|;
block|}
return|return
name|ri
return|;
block|}
specifier|public
name|void
name|setObserver
parameter_list|(
name|RepositoryInitializerObserver
name|observer
parameter_list|)
block|{
name|this
operator|.
name|observer
operator|=
name|observer
expr_stmt|;
block|}
interface|interface
name|RepositoryInitializerObserver
block|{
name|void
name|newRepositoryInitializer
parameter_list|(
name|RepositoryInitializer
name|ri
parameter_list|)
function_decl|;
block|}
block|}
end_class

end_unit

