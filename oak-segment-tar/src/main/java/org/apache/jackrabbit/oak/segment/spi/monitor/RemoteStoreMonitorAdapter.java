begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
name|segment
operator|.
name|spi
operator|.
name|monitor
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteStoreMonitorAdapter
implements|implements
name|RemoteStoreMonitor
block|{
annotation|@
name|Override
specifier|public
name|void
name|requestCount
parameter_list|()
block|{
comment|// Intentionally left blank
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestError
parameter_list|()
block|{
comment|// Intentionally left blank
block|}
annotation|@
name|Override
specifier|public
name|void
name|requestDuration
parameter_list|(
name|long
name|duration
parameter_list|,
name|TimeUnit
name|timeUnit
parameter_list|)
block|{
comment|// Intentionally left blank
block|}
block|}
end_class

end_unit

