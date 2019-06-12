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
name|api
operator|.
name|observation
package|;
end_package

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
name|observation
operator|.
name|EventListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|observation
operator|.
name|ObservationManager
import|;
end_import

begin_comment
comment|/**  * Jackrabbit specific extensions to {@link javax.jcr.observation.ObservationManager}.  */
end_comment

begin_interface
specifier|public
interface|interface
name|JackrabbitObservationManager
extends|extends
name|ObservationManager
block|{
comment|/**      * Adds an event listener that listens for the events specified      * by the passed {@link JackrabbitEventFilter}.      *<p>      * In addition to the<code>EventFilter</code>, the set of events reported      * will be further filtered by the access rights of the      * current<code>Session</code>.      *<p>      * See {@link JackrabbitEventFilter} for a description of the filtering parameters available.      *<p>      * The filter of an already-registered<code>EventListener</code> can be      * changed at runtime by re-registering the same<code>EventListener</code>      * object (i.e. the same actual Java object) with a new filter.      * The implementation must ensure that no events are lost during the changeover.      *<p>      * In addition to the filters placed on a listener above, the scope of      * observation support, in terms of which parts of a workspace are observable, may also      * be subject to implementation-specific restrictions. For example, in some      * repositories observation of changes in the<code>jcr:system</code>      * subgraph may not be supported.      *      * @param listener     an {@link EventListener} object.      * @param filter       an {@link JackrabbitEventFilter} object.      * @throws RepositoryException If an error occurs.      */
name|void
name|addEventListener
parameter_list|(
name|EventListener
name|listener
parameter_list|,
name|JackrabbitEventFilter
name|filter
parameter_list|)
throws|throws
name|RepositoryException
function_decl|;
block|}
end_interface

end_unit

