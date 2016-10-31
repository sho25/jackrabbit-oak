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
name|jcr
operator|.
name|observation
operator|.
name|filter
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
name|api
operator|.
name|observation
operator|.
name|JackrabbitEventFilter
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
name|jcr
operator|.
name|observation
operator|.
name|OakEventFilterImpl
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
name|jcr
operator|.
name|observation
operator|.
name|ObservationManagerImpl
import|;
end_import

begin_comment
comment|/**  * Static factory that allows wrapping a JackrabbitEventFilter into an  * OakEventFilter that contains some oak specific extensions.  *<p>  * The resulting filter can subsequently be used in  * ObservationManagerImpl.addEventListener as usual.  *   * @see ObservationManagerImpl#addEventListener(javax.jcr.observation.EventListener,  *      JackrabbitEventFilter)  */
end_comment

begin_class
specifier|public
class|class
name|FilterFactory
block|{
comment|/**      * Wrap a JackrabbitEventFilter into its corresponding oak extension,      * OakEventFilter, on which some Oak specific observation filter extensions      * can then be used.      *       * @param baseFilter      *            the base filter which contains other properties. Changes to      *            the resulting oak filter "write-through" to the underlying      *            baseFilter (for the features covered by the underlying) and      *            similarly changes to the baseFilter are seen by the resulting      *            oak filter. Note that this "write-through" behavior does no       *            longer apply after a listener was registered, ie changing      *            a filter after registration doesn't alter it for that listener.      * @return an OakEventFilter upon which Oak specific observation filtering      *         extensions can be activated and then used when adding an      *         EventListener with the ObservationManagerImpl.      */
specifier|public
specifier|static
name|OakEventFilter
name|wrap
parameter_list|(
name|JackrabbitEventFilter
name|baseFilter
parameter_list|)
block|{
return|return
operator|new
name|OakEventFilterImpl
argument_list|(
name|baseFilter
argument_list|)
return|;
block|}
block|}
end_class

end_unit

