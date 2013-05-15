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
name|version
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
name|annotation
operator|.
name|Nonnull
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
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
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
name|spi
operator|.
name|state
operator|.
name|NodeBuilder
import|;
end_import

begin_comment
comment|/**  *<i>Inspired by Jackrabbit 2.x</i>  *<p/>  * This Class implements a version selector that selects a version by creation  * date. The selected version is the latest that is older or equal than the  * given date. If no version could be found<code>null</code> is returned  * unless the<code>returnLatest</code> flag is set to<code>true</code>, where  * the latest version is returned.  *<pre>  * V1.0 - 02-Sep-2006  * V1.1 - 03-Sep-2006  * V1.2 - 05-Sep-2006  *  * new DateVersionSelector("03-Sep-2006").select() -> V1.1  * new DateVersionSelector("04-Sep-2006").select() -> V1.1  * new DateVersionSelector("01-Sep-2006").select() -> null  * new DateVersionSelector("01-Sep-2006", true).select() -> V1.2  * new DateVersionSelector(null, true).select() -> V1.2  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|DateVersionSelector
implements|implements
name|VersionSelector
block|{
comment|/**      * a version date hint      */
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
comment|/**      * Creates a<code>DateVersionSelector</code> that will select the latest      * version of all those that are older than the given timestamp.      *      * @param timestamp reference timestamp      */
specifier|public
name|DateVersionSelector
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeBuilder
name|select
parameter_list|(
annotation|@
name|Nonnull
name|NodeBuilder
name|history
parameter_list|)
throws|throws
name|RepositoryException
block|{
name|long
name|latestDate
init|=
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|NodeBuilder
name|latestVersion
init|=
literal|null
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|history
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|v
init|=
name|history
operator|.
name|getChildNode
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
name|JcrConstants
operator|.
name|JCR_ROOTVERSION
argument_list|)
condition|)
block|{
comment|// ignore root version
continue|continue;
block|}
name|long
name|c
init|=
name|v
operator|.
name|getProperty
argument_list|(
name|JcrConstants
operator|.
name|JCR_CREATED
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|>
name|latestDate
operator|&&
name|c
operator|<=
name|timestamp
condition|)
block|{
name|latestDate
operator|=
name|c
expr_stmt|;
name|latestVersion
operator|=
name|v
expr_stmt|;
block|}
block|}
return|return
name|latestVersion
return|;
block|}
block|}
end_class

end_unit

