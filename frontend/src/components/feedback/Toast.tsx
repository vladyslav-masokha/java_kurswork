type ToastProps = {
  error: string;
  message: string;
};

export function Toast({ error, message }: ToastProps) {
  if (!message && !error) {
    return null;
  }

  return <div className={error ? 'toast error' : 'toast'}>{error || message}</div>;
}

