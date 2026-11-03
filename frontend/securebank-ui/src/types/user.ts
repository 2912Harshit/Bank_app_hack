export interface DemoUser {
  userId: string;
  customerName: string;
  email: string;
  role: string;
}

export const DEMO_USERS: DemoUser[] = [
  {
    userId: 'user-001',
    customerName: 'Aarav Sharma',
    email: 'aarav.sharma@example.com',
    role: 'Primary Customer'
  },
  {
    userId: 'user-002',
    customerName: 'Priya Patel',
    email: 'priya.patel@example.com',
    role: 'Business Customer'
  },
  {
    userId: 'user-003',
    customerName: 'Rohan Verma',
    email: 'rohan.verma@example.com',
    role: 'Standard Customer'
  },
  {
    userId: 'user-004',
    customerName: 'Neha Gupta',
    email: 'neha.gupta@example.com',
    role: 'Account Under Review'
  }
];

