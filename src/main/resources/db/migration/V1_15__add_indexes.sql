CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_flag_details_category_id ON public.flag_details (category_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_flag_service_service_id_flag_code ON public.flag_service (service_id, flag_code);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_flag_details_id_category_id ON public.flag_details (id, category_id);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_flag_service_upper_service_id ON public.flag_service (UPPER(service_id));